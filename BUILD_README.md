# GabáyApp Build Guide

This document explains how to build and run the GabáyApp on different platforms.

## Directory Structure

```
GabáyApp/
│
├── src/                   # Java source files
├── bin/                   # Compiled class files
├── lib/                   # Libraries and resources
│   ├── fonts/             # Font files
│   └── images/            # Images used by the fonts (if any)
├── resources/             # Application resources
│   └── images/            # Image resources
│       ├── Candidate Search/  # Candidate search page images
│       ├── Buttons Icon/      # Button icons
│       └── candidates/        # Candidate profile images
├── .vscode/               # VS Code configuration
├── build.bat              # Windows build script
├── run.bat                # Windows run script
├── cleanup.bat            # Windows cleanup script
└── .gitignore             # Git ignore configuration
```

## Building the Application

### Windows

1. Use the provided batch scripts:
   ```
   build.bat     # Compiles the source files into the bin directory
   run.bat       # Runs the compiled application
   cleanup.bat   # Removes compiled .class files from the src directory
   ```

### macOS/Linux

1. Compile the source files:
   ```bash
   mkdir -p bin
   javac -d bin -cp ".:lib/*" src/*.java
   ```

2. Run the application:
   ```bash
   java -cp "bin:lib/*" App
   ```

## Resource Handling

The application uses a `ResourceHelper` class to ensure resources are accessed in a platform-independent way. This handles path separators and directory structure differences between operating systems.

## Dependencies

- Java 17 or later
- Inter fonts (included in lib/fonts)
- No external libraries required

## Common Issues and Solutions

### Issue: Missing Resources

If you encounter "resource not found" errors, ensure:
- The directory structure matches what's described above
- All required resource files are in their correct locations
- Case sensitivity is correct (especially important on Linux/macOS)

### Issue: Font Loading Problems

If fonts fail to load:
- Check that all font files exist in the lib/fonts directory
- Try running the application with Java's font debugging: `java -Ddebug.font=true -cp "bin:lib/*" App`

## Contributing Guidelines

1. Use the ResourceHelper class for all resource access to ensure cross-platform compatibility
2. Keep .class files out of version control (they will be in the bin/ directory)
3. Run cleanup.bat before pushing changes to remove any stray .class files
4. Maintain the directory structure as outlined above 