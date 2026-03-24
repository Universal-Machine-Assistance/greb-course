const { chromium } = require("playwright");

async function lockExportState(page) {
  await page.addStyleTag({
    content: `
      html.pdf-exporting, body.pdf-exporting { overflow: visible !important; }
      html.pdf-exporting { --toolbar-h: 0px !important; }
      html.pdf-exporting .toolbar,
      html.pdf-exporting .nav-btn,
      html.pdf-exporting .spread-dots,
      html.pdf-exporting .spread-indicator,
      html.pdf-exporting .toc-overlay,
      html.pdf-exporting .toc-wrapper,
      html.pdf-exporting .omni-scrim,
      html.pdf-exporting .omni-bar,
      html.pdf-exporting .omni-results,
      html.pdf-exporting .omni-embed-section,
      html.pdf-exporting .omni-embed-host,
      html.pdf-exporting .omni-embed-bar {
        display: none !important;
      }
      html.pdf-exporting .reader {
        top: 0 !important;
      }
      html.pdf-exporting .spread.active,
      html.pdf-exporting .spread.going-back,
      html.pdf-exporting .spread.spread-preview-hover,
      html.pdf-exporting .spread.spread-exiting {
        zoom: 1 !important;
        transform: none !important;
      }
    `,
  });

  await page.evaluate(() => {
    document.documentElement.classList.add("pdf-exporting");
    document.body.classList.add("pdf-exporting");
    document.documentElement.classList.remove("doc-chrome-idle", "ui-hidden");

    const reader = document.querySelector(".reader");
    if (reader) {
      reader.style.setProperty("--doc-zoom", "1");
      reader.style.setProperty("--doc-text-scale", "1");
      reader.style.setProperty("--doc-pan-x", "0px");
      reader.style.setProperty("--doc-pan-y", "0px");
      reader.style.setProperty("--canvas-scale", "1");
    }
  });
}

async function waitForAssets(page) {
  await page.evaluate(async () => {
    const timeoutMs = 15000;
    const withTimeout = (promise) =>
      Promise.race([
        promise,
        new Promise((resolve) => setTimeout(resolve, timeoutMs)),
      ]);

    // Fonts can shift/hide hero composition if not ready.
    if (document.fonts && document.fonts.ready) {
      await withTimeout(document.fonts.ready);
    }

    const imgs = Array.from(document.images || []);
    await withTimeout(
      Promise.all(
        imgs.map(async (img) => {
          try {
            if (!img.complete) {
              await new Promise((resolve) => {
                img.addEventListener("load", resolve, { once: true });
                img.addEventListener("error", resolve, { once: true });
                setTimeout(resolve, 5000);
              });
            }
            if (img.decode) {
              await img.decode().catch(() => {});
            }
          } catch (_) {
            // Best-effort: continue even if one image fails.
          }
        }),
      ),
    );
  });
}

async function waitForLayoutStability(page) {
  let stableHits = 0;
  let prevSig = "";

  for (let i = 0; i < 10; i += 1) {
    const sig = await page.evaluate(() => {
      const pages = Array.from(document.querySelectorAll(".page")).slice(0, 12);
      return pages
        .map((pg) => {
          const r = pg.getBoundingClientRect();
          const body = pg.querySelector(".page-body");
          return [
            pg.id || "",
            Math.round(r.width),
            Math.round(r.height),
            body ? body.scrollHeight : 0,
            body ? body.clientHeight : 0,
          ].join(":");
        })
        .join("|");
    });

    if (sig === prevSig) {
      stableHits += 1;
      if (stableHits >= 3) {
        return;
      }
    } else {
      stableHits = 0;
      prevSig = sig;
    }
    await page.waitForTimeout(180);
  }
}

async function main() {
  const url = process.argv[2];
  const outPath = process.argv[3];
  const pageRange = process.argv[4]; // optional: "1", "1-3", "2,5,8"

  if (!url || !outPath) {
    throw new Error(
      "Usage: node scripts/export_pdf.js <url> <outPath> [pageRange]",
    );
  }

  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1800, height: 1200 } });

  await page.goto(url, { waitUntil: "networkidle", timeout: 120000 });
  await lockExportState(page);
  await waitForAssets(page);
  await page.waitForTimeout(700);
  await page.emulateMedia({ media: "print" });
  await lockExportState(page);
  await waitForAssets(page);
  await waitForLayoutStability(page);
  await page.waitForTimeout(250);

  const pdfOpts = {
    path: outPath,
    printBackground: true,
    preferCSSPageSize: true,
  };
  if (pageRange) {
    pdfOpts.pageRanges = pageRange;
  }

  await page.pdf(pdfOpts);
  await browser.close();
}

main().catch((err) => {
  console.error(err?.stack || String(err));
  process.exit(1);
});
