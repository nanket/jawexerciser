# Jaw Exerciser MVP - Android App

An MVP mobile application for Android that automatically tracks jaw exercise repetitions using real-time camera-based feedback with TensorFlow Lite face landmark detection.

## Features

### Core Functionality
- **Real-time Jaw Exercise Tracking**: Uses front-facing camera and TensorFlow Lite for face landmark detection
- **Three Exercise Types**: 
  - Jaw Opener: Open mouth wide and hold
  - Chin Lift: Lift chin while keeping mouth closed
  - Side-to-Side Jaw Shift: Move jaw from side to side
- **Automatic Rep Counting**: AI-powered detection of exercise repetitions
- **Session Tracking**: Records reps, duration, and exercise type

### User Interface
- **Material Design 3**: Clean, modern interface following Google's design guidelines
- **Home Dashboard**: Exercise selection with card-based layout
- **Exercise Session Screen**: Real-time camera preview with overlay feedback
- **Progress History**: View past 7 days of workout sessions with statistics

### Technical Architecture
- **MVVM Architecture**: Clean separation of concerns with ViewModels and LiveData
- **Room Database**: Local storage for sessions, exercises, and repetition data
- **CameraX Integration**: Modern camera API for reliable front-camera access
- **TensorFlow Lite**: On-device AI inference for face landmark detection
- **Kotlin Coroutines**: Asynchronous processing for smooth UI experience

## Technical Requirements

### Minimum Requirements
- Android API Level 24 (Android 7.0)
- Front-facing camera
- 2GB RAM minimum
- 100MB storage space

### Dependencies
- Kotlin 1.9.10
- Android Gradle Plugin 8.2.0
- TensorFlow Lite 2.14.0
- CameraX 1.3.1
- Room Database 2.6.1
- Material Design Components 1.11.0

## Setup Instructions

### 1. Clone and Build
```bash
git clone <repository-url>
cd jaw-exerceiser
./gradlew build
```

### 2. TensorFlow Lite Model
The app is designed to work with a face landmark detection model. For development, it includes a mock detector that allows testing without the actual model file.

To use a real TensorFlow Lite model:
1. Download a face landmark detection model (e.g., MediaPipe Face Mesh)
2. Place the `.tflite` file in `app/src/main/assets/` as `face_landmark_model.tflite`
3. The app will automatically load and use the real model

### 3. Permissions
The app requires camera permission for jaw exercise tracking. Permission is requested at runtime when starting an exercise session.

## Architecture Overview

### Data Layer
- **Room Database**: Stores exercises, sessions, and repetition data
- **Repository Pattern**: Abstracts data access with clean interfaces
- **Entity Classes**: Exercise, Session, and Rep models

### ML/AI Layer
- **FaceLandmarkDetector**: TensorFlow Lite integration for face detection
- **JawExerciseDetector**: Algorithm for detecting jaw movements and counting reps
- **Exercise Detection Logic**: Specific algorithms for each exercise type

### UI Layer
- **MainActivity**: Home screen with exercise selection
- **ExerciseActivity**: Camera-based exercise session with real-time feedback
- **HistoryActivity**: Progress tracking and session history
- **Custom Views**: ExerciseOverlayView for drawing landmarks and feedback

### Camera Integration
- **CameraManager**: Wrapper around CameraX for simplified camera operations
- **Real-time Processing**: Frame-by-frame analysis for continuous tracking
- **Front Camera Focus**: Optimized for selfie-style jaw exercise tracking

## Exercise Detection Algorithms

### Jaw Opener
- Measures distance between upper and lower lip landmarks
- Detects mouth opening beyond threshold
- Counts complete open-close cycles

### Chin Lift
- Tracks vertical distance between chin and nose tip
- Detects upward chin movement
- Measures hold duration and return to baseline

### Side-to-Side Jaw Shift
- Analyzes asymmetry between left and right jaw landmarks
- Detects lateral jaw movement
- Counts side-to-side motion cycles

## Development Notes

### Mock Mode
The app includes a mock face detection mode for development and testing without requiring an actual TensorFlow Lite model. This allows:
- UI testing and development
- Algorithm refinement
- Performance optimization
- User experience validation

### Production Deployment
For production use:
1. Integrate a trained face landmark detection model
2. Fine-tune detection thresholds based on user testing
3. Add model optimization for better performance
4. Implement additional exercise types as needed

### Performance Considerations
- On-device processing ensures privacy and low latency
- Optimized for real-time performance on mid-range devices
- Efficient memory usage with proper lifecycle management
- Background processing using Kotlin coroutines

## Future Enhancements

### Potential Features
- Exercise difficulty levels and progression tracking
- Social features and challenges
- Integration with health apps
- Voice guidance and audio feedback
- Additional jaw exercise types
- Detailed analytics and progress charts

### Technical Improvements
- Model quantization for better performance
- Custom TensorFlow Lite model training
- Advanced computer vision techniques
- Cloud sync for cross-device progress
- Wear OS companion app

## License

This project is developed as an MVP demonstration of AI-powered fitness tracking using modern Android development practices and machine learning integration.
