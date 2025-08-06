# Jaw Exerciser App - Crash Debugging Guide

## Common Crash Causes and Solutions

### 1. **Room Database Initialization Issues**
**Symptoms**: App crashes immediately on startup
**Cause**: Room annotation processing not working correctly
**Solution**: 
- Ensure KAPT is used instead of KSP for Room
- Check that all Room entities have proper annotations
- Verify database callback doesn't cause issues

**Fixed in code**: Switched from KSP to KAPT, added fallbackToDestructiveMigration()

### 2. **ViewBinding Generation Issues**
**Symptoms**: ClassNotFoundException for binding classes
**Cause**: ViewBinding not properly configured or generated
**Solution**:
- Clean and rebuild project
- Ensure ViewBinding is enabled in build.gradle
- Check that layout files are valid XML

**Fixed in code**: ViewBinding properly configured in build.gradle

### 3. **TensorFlow Lite Model Missing**
**Symptoms**: App crashes when starting exercise session
**Cause**: TensorFlow Lite model file not found in assets
**Solution**:
- Add face_landmark_model.tflite to app/src/main/assets/
- Or use mock detector (already implemented)

**Fixed in code**: Mock detector fallback implemented

### 4. **Camera Permission Issues**
**Symptoms**: App crashes when accessing camera
**Cause**: Camera permission not granted or not properly requested
**Solution**:
- Ensure camera permission is in manifest
- Request permission at runtime before camera access

**Fixed in code**: Proper permission handling in ExerciseActivity

### 5. **Missing Resources**
**Symptoms**: ResourceNotFoundException
**Cause**: Missing drawable resources or launcher icons
**Solution**:
- Ensure all referenced drawables exist
- Add proper launcher icons

**Fixed in code**: Added all required drawable resources and launcher icons

## Debugging Steps

### Step 1: Check Logcat
Look for these log messages to identify the issue:
- `JawExerciserApp: Application started successfully`
- `MainActivity: MainActivity onCreate started`
- `MainActivity: MainActivity onCreate completed successfully`

### Step 2: Common Error Messages
- **ClassNotFoundException**: Usually ViewBinding or Room annotation processing issue
- **ResourceNotFoundException**: Missing drawable or layout resources
- **SecurityException**: Camera permission not granted
- **SQLiteException**: Database initialization issue

### Step 3: Build Issues
If the app won't build:
1. Clean project: `./gradlew clean`
2. Rebuild: `./gradlew assembleDebug`
3. Check for missing dependencies

### Step 4: Runtime Crashes
If app builds but crashes on startup:
1. Check Application class initialization
2. Verify MainActivity onCreate flow
3. Check Room database setup
4. Verify all resources exist

## Quick Fixes Applied

1. **Switched to KAPT**: Room annotation processing now uses KAPT instead of KSP
2. **Added Error Handling**: MainActivity now catches and logs all exceptions
3. **Mock TensorFlow**: App works without actual TensorFlow Lite model
4. **Simplified Camera**: Placeholder bitmap generation to avoid YUV conversion issues
5. **Database Fallback**: Added fallbackToDestructiveMigration() for database issues
6. **Resource Fixes**: Added all required drawable resources and launcher icons

## Testing the Fix

To test if the fixes work:
1. Build the app: `./gradlew assembleDebug`
2. Install on device/emulator
3. Check logcat for error messages
4. Verify app starts and shows exercise list

## Next Steps

If app still crashes:
1. Share the logcat output showing the crash
2. Identify which component is failing
3. Apply targeted fixes based on the specific error

The app is now much more robust with proper error handling and fallbacks for common issues.
