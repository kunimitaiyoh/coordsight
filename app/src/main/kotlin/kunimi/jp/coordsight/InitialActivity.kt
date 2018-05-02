package kunimi.jp.coordsight

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class InitialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (permission) {
            SightActivity.start(this)
        } else {
            requestPermission()
        }
        finish()
    }

    fun requestPermission() {
        val shouldShowRequest = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (shouldShowRequest) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA), REQUEST_PERMISSION)
        } else {
            Toast.makeText(this, "permission required.", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SightActivity.start(this)
                } else {
                    Toast.makeText(this, "permission required.", Toast.LENGTH_SHORT)
                            .show()
                    this.finish()
                }
            }
        }
    }

    companion object {
        val REQUEST_PERMISSION: Int = 10
    }
}