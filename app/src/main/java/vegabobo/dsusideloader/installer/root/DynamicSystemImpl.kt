package vegabobo.dsusideloader.installer.root

import android.gsi.GsiProgress
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.image.IDynamicSystemService
import vegabobo.dsusideloader.service.PrivilegedProvider

open class DynamicSystemImpl : IDynamicSystemService {

    override fun asBinder(): IBinder? {
        return null
    }

    override fun getInstallationProgress(): GsiProgress {
        return PrivilegedProvider.getServiceBlocking().installationProgress
    }

    override fun abort(): Boolean {
        return PrivilegedProvider.getServiceBlocking().abort()
    }

    override fun isInUse(): Boolean {
        return PrivilegedProvider.getServiceBlocking().isInUse
    }

    override fun isInstalled(): Boolean {
        return PrivilegedProvider.getServiceBlocking().isInstalled
    }

    override fun isEnabled(): Boolean {
        return PrivilegedProvider.getServiceBlocking().isEnabled
    }

    override fun remove(): Boolean {
        return PrivilegedProvider.getServiceBlocking().remove()
    }

    override fun setEnable(enable: Boolean, oneShot: Boolean): Boolean {
        return PrivilegedProvider.getServiceBlocking().setEnable(enable, oneShot)
    }

    override fun finishInstallation(): Boolean {
        return PrivilegedProvider.getServiceBlocking().finishInstallation()
    }

    override fun startInstallation(dsuSlot: String): Boolean {
        return PrivilegedProvider.getServiceBlocking().startInstallation(dsuSlot)
    }

    override fun createPartition(name: String, size: Long, readOnly: Boolean): Int {
        return PrivilegedProvider.getServiceBlocking().createPartition(name, size, readOnly)
    }

    override fun closePartition(): Boolean {
        return PrivilegedProvider.getServiceBlocking().closePartition()
    }

    override fun setAshmem(fd: ParcelFileDescriptor, size: Long): Boolean {
        return PrivilegedProvider.getServiceBlocking().setAshmem(fd, size)
    }

    override fun submitFromAshmem(bytes: Long): Boolean {
        return PrivilegedProvider.getServiceBlocking().submitFromAshmem(bytes)
    }

    override fun suggestScratchSize(): Long {
        return PrivilegedProvider.getServiceBlocking().suggestScratchSize()
    }

    fun forceStopDSU() {
        PrivilegedProvider.getServiceBlocking().forceStopPackage("com.android.dynsystem")
    }
}
