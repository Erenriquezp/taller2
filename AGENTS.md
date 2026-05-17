# AGENTS.md

## Project snapshot
- Plain Java image-processing exercises under `src/`; there is no Maven/Gradle build.
- IntelliJ uses `taller-2.iml`, and `src` is the only source root.
- The code is organized by demo/topic, not as one shared library: `practice/`, `taller/`, `tarea1/`, `tarea2/`, `tarea3/reduccion/`, `transparence/`, and `matrix/`.

## What to read first
- `NOTES.md` at the repo root for the best project-wide map and formulas.
- `src/practice/NOTES.md` and `src/tarea1/NOTES_FILTROS.md` for the canonical filter recipes.
- Representative implementations: `src/tarea2/TareaFiltrosAvanzados.java`, `src/taller/Color.java`, `src/taller/Convolucion.java`, `src/taller/images/Kernels.java`, `src/transparence/Blending.java`.

## Core patterns to preserve
- Most programs are standalone `main` demos that read an image, process pixels in nested `for (y) for (x)` loops, and write a new file with `ImageIO.write(...)`.
- Channel handling is always bit-based: extract with `(pixel >> 16) & 0xFF`, `(pixel >> 8) & 0xFF`, `pixel & 0xFF`, and rebuild with shifts and `|`.
- Use `BufferedImage.TYPE_INT_ARGB` only when alpha must survive; otherwise `TYPE_INT_RGB` is common.
- Many outputs are written back into `src/<module>/images/`; keep that convention unless the user asks to change it.

## Repo-specific quirks
- Some source files do not match their folder/package name exactly: for example `src/tarea1/FiltrosImagen.java` and `src/tarea1/GeneradorDegradados.java` declare `package taller;`.
- Intentional spelling oddities exist and should be preserved unless renaming is explicitly requested: `Filtos`, `ConvulucionOp`, etc.
- Several files contain commented alternate implementations; this repo favors exploratory demos over strict cleanup.

## Developer workflow
- Java toolchain currently present in the workspace is Java 8. Always pass `-encoding UTF-8` to `javac`.
- A full-repo compile on this JDK fails because some `src/practice/*.java` files already use `Math.clamp`; compile the specific demo you are touching, or replace those calls first.
- From the repo root, compile and run a targeted demo by its declared package name:
  ```powershell
  New-Item -ItemType Directory -Force out | Out-Null
  javac -encoding UTF-8 -d out -sourcepath src src\tarea2\TareaFiltrosAvanzados.java
  java -cp out tarea2.TareaFiltrosAvanzados
  ```
- Many demos depend on relative paths like `src/taller/images/original.png`; run them from the repo root so image IO resolves correctly.

## Convolution and filter conventions
- Convolution demos use 3x3 kernels and often skip border pixels (`y = 1` to `alto - 1`).
- `src/taller/images/Kernels.java` is the shared kernel reference; prefer its constants when matching existing behavior.
- `src/transparence/Blending.java` and the alpha-based filters rely on keeping the original background/foreground channel math intact.

## When changing code
- Match the current Spanish naming and the existing file layout.
- Prefer small, local edits that keep the demo outputs and file paths consistent.
- If a change touches image math, compare it against the formulas documented in the repo notes before rewriting the implementation.

