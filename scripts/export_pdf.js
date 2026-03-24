const { chromium } = require("playwright");

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

async function main() {
  const url = process.argv[2];
  const outPath = process.argv[3];

  if (!url || !outPath) {
    throw new Error("Usage: node scripts/export_pdf.js <url> <outPath>");
  }

  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1800, height: 1200 } });

  await page.goto(url, { waitUntil: "networkidle", timeout: 120000 });
  await waitForAssets(page);
  await page.waitForTimeout(900);
  await page.emulateMedia({ media: "print" });
  await waitForAssets(page);
  await page.waitForTimeout(350);

  await page.pdf({
    path: outPath,
    printBackground: true,
    preferCSSPageSize: true,
  });

  await browser.close();
}

main().catch((err) => {
  console.error(err?.stack || String(err));
  process.exit(1);
});
