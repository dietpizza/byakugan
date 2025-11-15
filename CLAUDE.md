# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Byakugan is an Android manga reader application built with Kotlin. The app is designed to read manga from archive files (ZIP/CBZ) and display them in a grid-based library interface.

**Package**: `com.dietpizza.byakugan`
**Min SDK**: 33 (Android 13)
**Target SDK**: 36
**Language**: Kotlin (JVM target 11)

## Build Commands

```bash
# Build the app (debug)
./gradlew assembleDebug

# Build release APK (with ProGuard)
./gradlew assembleRelease

# Install debug build to connected device/emulator
./gradlew installDebug

# Run tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean build artifacts
./gradlew clean

# Build and install debug, then run
./gradlew installDebug && adb shell am start -n com.dietpizza.byakugan/.MainActivity
```

## Architecture

### Core Components

**MainActivity** (`MainActivity.kt`)
- Entry point and main UI controller
- Uses View Binding (`ActivityMainBinding`)
- Implements Material 3 dynamic colors
- Configures RecyclerView with `GridLayoutManager` (2 columns)
- Currently displays placeholder manga cards

**MangaCardAdapter** (`adapters/MangaCardAdapter.kt`)
- RecyclerView adapter for displaying manga cards in grid layout
- Uses View Binding (`WidgetMangaCardBinding`)
- Binds manga metadata (image, name, size) to card views

**Data Model** (`models/MangaCardModel.kt`)
- Data class representing manga card metadata
- Fields: `imageResource` (Int), `name` (String), `size` (String)

### Services

**MangaParserService** (`services/MangaParserService.kt`)
- Validates and parses manga archive files (ZIP/CBZ)
- `isSupportedFormat()`: Checks if file extension is supported
- Metadata extraction not yet implemented

**StorageService** (`services/StorageService.kt`)
- Handles Android storage URIs and file path conversions
- `getFilePathFromUri()`: Converts content:// and file:// URIs to file paths
- Supports document URIs, tree URIs, and MediaStore URIs
- Handles primary storage and secondary storage (SD cards)

### Constants

**AppConstants** (`AppConstants.kt`)
- `SupportedFileTypes`: `["zip", "cbz"]`
- `SupportedImagesTypes`: `["jpg", "jpeg", "png", "webp", "bmp"]`

## UI Structure

**Layouts**:
- `activity_main.xml`: Main screen with custom toolbar and RecyclerView
- `widget_manga_card.xml`: Individual manga card with MaterialCardView, image, title, and size

**View Binding**: Enabled project-wide. Always use binding instead of findViewById.

**Material 3**: Uses Material Design 3 components with dynamic color support (Android 12+)

## ProGuard Configuration

Release builds have minification and resource shrinking enabled. ProGuard rules (`proguard-rules.pro`):
- Keeps all model classes (`com.dietpizza.byakugan.models.**`)
- Keeps custom View constructors
- Preserves line numbers for crash reports

## Key Development Notes

1. **View Binding**: All new layouts must use View Binding. Generate binding classes are named as `<LayoutName>Binding` (e.g., `ActivityMainBinding`).

2. **Storage Access**: When implementing file picking or storage access, use `StorageService.getFilePathFromUri()` to handle all URI types correctly.

3. **Supported Formats**: Only implement features for ZIP and CBZ archives containing JPG/JPEG/PNG/WebP/BMP images.

4. **Material 3**: Always use Material 3 components (e.g., `MaterialCardView`, `ShapeableImageView`) and Material color attributes (`?attr/colorSurface`, `?attr/colorOnSurface`, etc.).

5. **Grid Layout**: The manga library uses a 2-column grid layout. Consider this when designing card dimensions and content.

6. **Release Builds**: Test ProGuard configuration when adding new models or custom views to ensure they're properly kept.

## Pending Implementation

The following areas are marked TODO or have placeholder implementations:
- Manga metadata extraction in `MangaParserService.getMangaMetadata()`
- Settings button functionality in MainActivity
- Actual manga file loading and parsing
- Image extraction from archive files
- Manga detail/reader view
