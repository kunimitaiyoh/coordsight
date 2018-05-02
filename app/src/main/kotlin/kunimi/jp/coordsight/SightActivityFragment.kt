package kunimi.jp.coordsight

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

class SightActivityFragment : Fragment() {
    var mCameraDevice: CameraDevice? = null

    lateinit var mTextureView: TextureView
    lateinit var mMessage: TextView

    val mBackgroundHandler = Handler()
    var mCaptureSession: CameraCaptureSession? = null

    val mStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            cameraDevice.close()
            mCameraDevice = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_sight, container, false)
        mTextureView = root.findViewById(R.id.texture) as TextureView
        mTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }
        }
        mMessage = root.findViewById(R.id.message) as TextView
        mMessage.setText(R.string.app_name)

        return root
    }

    fun openCamera() {
        try {
            val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val selectedCameraId = manager.cameraIdList
                    .firstOrNull { manager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK }
            if (selectedCameraId != null) {

                manager.openCamera(selectedCameraId, mStateCallback, mBackgroundHandler)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun createCameraPreviewSession() {
        val texture = mTextureView.surfaceTexture
        texture.setDefaultBufferSize(mTextureView.width, mTextureView.height)
        val surface = Surface(texture)

        try {
            val builder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            builder.addTarget(surface)
            val previewRequest = builder.build()

            mCameraDevice!!.createCaptureSession(Arrays.asList(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    if (null == mCameraDevice) {
                        return
                    }

                    mCaptureSession = session

                    try {
                        session.setRepeatingRequest(previewRequest, null, null)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}
