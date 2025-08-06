#!/bin/bash

# Simple build script for Jaw Exerciser app
# This script attempts to build the app using available tools

echo "🔨 Building Jaw Exerciser App..."

# Check if gradle is available in system
if command -v gradle &> /dev/null; then
    echo "✅ Using system Gradle"
    gradle assembleDebug
elif command -v ./gradlew &> /dev/null; then
    echo "✅ Using Gradle wrapper"
    ./gradlew assembleDebug
else
    echo "❌ No Gradle found. Please install Gradle or fix the wrapper."
    echo ""
    echo "To fix the wrapper issue:"
    echo "1. Download gradle-wrapper.jar manually"
    echo "2. Place it in gradle/wrapper/"
    echo "3. Run ./gradlew assembleDebug"
    exit 1
fi

# Check if build was successful
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "✅ Build successful! APK created at:"
    echo "   app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📱 To install on device:"
    echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Build failed. Check the error messages above."
    exit 1
fi
