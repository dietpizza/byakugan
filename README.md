# Byakugan

Byakugan is a lightweight Android manga reader (Kotlin) that displays manga stored in archive
files (ZIP/CBZ) using a grid-based library UI.

Key features

- Simple, responsive Jetpack Compose UI with a 2-column grid library
- Supports reading manga from ZIP/CBZ archives containing common image formats (jpg, jpeg, png,
  webp, bmp)
- View binding used for RecyclerView card layout where applicable

Quick start

1. Install JDK 11 and Android SDK (min SDK 33, target SDK 36).
2. Build debug APK:
   ./gradlew assembleDebug
3. Install to a connected device/emulator:
   ./gradlew installDebug
4. Run instrumented tests on a device/emulator:
   ./gradlew connectedAndroidTest

Project structure

- app/: Android application module (com.dietpizza.byakugan)
- assets/ and build/: build artifacts and static assets
- services/: storage and manga parser helper services
- adapters/: RecyclerView adapters and view binding usage
- models/: simple data models representing manga cards

Development notes

- StorageService.getFilePathFromUri() handles content://, document and tree URIs.
- Supported formats: zip, cbz (images: jpg, jpeg, png, webp, bmp).
- TODOs: metadata extraction from archives, image extraction and reader view, settings screen.

Contributing

- Open issues and pull requests are welcome. Keep changes small and focused; run existing Gradle
  tasks to verify builds and tests.

License

- WTFPL - Do What The Fuck You Want To Public License

