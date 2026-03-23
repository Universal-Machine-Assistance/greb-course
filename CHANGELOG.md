# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-03-23

### Added
- Presentation mode: widescreen 16:9 layout with component-level slides, zoom slider, maximize toggle, and section mode in toolbar
- Mobile UX: swipe navigation, tap left/right half to navigate, compact toolbar, bigger dots, hidden print/present controls
- Harmonia user manual — full documentation for Nueva Acrópolis academic management system
- Greb Docs manual with Monaco editor integration and dock-style navigation dots
- OmniREPL documentation, code-block template, and 4 code reference pages (REPL/SQL/API examples)
- User profile docs and full-page screenshot views
- Image optimization system using ImageMagick with manifest tracking
- REST API for image management on Railway volume
- Back-to-catalog button and catalog card editor
- Cover images on catalog cards; screenshot variant for full-image pages (`object-fit: contain`)
- New modules: `core_boot`, `omnirepl_commands`, `pres_controls`, `pres_physics`, `pres_session`, `server_images`, `server_proxy`
- Kie AI image generation and OpenRouter API key support in `.env.example`

### Fixed
- Lazy-load Monaco Editor to unblock mobile page rendering
- Touch pan and swipe handling in presentation mode on mobile
- Invalid JS property access crash (`.-touches.length`) on mobile
- Page overflow issues across multiple content pages
- Static asset serving: allow `/js/` and `/css/` through middleware
- `Content-Type` for optimized images served as `image/webp`
- TIF image optimization with libtiff support and multi-layer handling
- API route ordering: move `/api/images` before `/:org/:slug`
- Cover template: spread bullet items as individual children
- Dockerfile: use Clojure base with Node for shadow-cljs build

### Changed
- Use Railway volume for image storage instead of Docker COPY
- Valentino course: enhanced risk family visuals, allergen/physical pages, cover layout redesign
- Add `wrap-params` and `wrap-content-type` middleware for proper request/MIME handling
- Retry errored images on optimize, not just pending
