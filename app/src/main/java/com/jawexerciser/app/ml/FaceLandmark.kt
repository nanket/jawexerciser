package com.jawexerciser.app.ml

data class FaceLandmark(
    val x: Float,
    val y: Float,
    val z: Float = 0f
)

data class FaceLandmarks(
    val landmarks: List<FaceLandmark>
) {
    companion object {
        // Key landmark indices for jaw tracking (based on MediaPipe Face Mesh)
        const val CHIN_BOTTOM = 175
        const val UPPER_LIP_TOP = 13
        const val LOWER_LIP_BOTTOM = 14
        const val LEFT_MOUTH_CORNER = 61
        const val RIGHT_MOUTH_CORNER = 291
        const val JAW_LEFT = 172
        const val JAW_RIGHT = 397
        const val NOSE_TIP = 1
    }
    
    fun getChinPoint(): FaceLandmark? = landmarks.getOrNull(CHIN_BOTTOM)
    fun getUpperLipPoint(): FaceLandmark? = landmarks.getOrNull(UPPER_LIP_TOP)
    fun getLowerLipPoint(): FaceLandmark? = landmarks.getOrNull(LOWER_LIP_BOTTOM)
    fun getLeftMouthCorner(): FaceLandmark? = landmarks.getOrNull(LEFT_MOUTH_CORNER)
    fun getRightMouthCorner(): FaceLandmark? = landmarks.getOrNull(RIGHT_MOUTH_CORNER)
    fun getLeftJawPoint(): FaceLandmark? = landmarks.getOrNull(JAW_LEFT)
    fun getRightJawPoint(): FaceLandmark? = landmarks.getOrNull(JAW_RIGHT)
    fun getNoseTip(): FaceLandmark? = landmarks.getOrNull(NOSE_TIP)
}
