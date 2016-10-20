// IPreviewService.aidl
package dev.nick.app.screencast;

// Declare any non-default types here with import statements

interface IPreviewService {
    void attach();
    void swapCamera();
    void detach();
}
