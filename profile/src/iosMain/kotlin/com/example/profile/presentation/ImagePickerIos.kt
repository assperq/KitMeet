package com.example.profile.presentation

import platform.UIKit.*
import platform.Foundation.*
import platform.darwin.NSObject
import kotlinx.cinterop.ObjCAction

//class ImagePickerIos : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol, ImagePicker {
//
//    private var callback: ((String) -> Unit)? = null
//
//    override fun pickImage(onImagePicked: (String) -> Unit) {
//        callback = onImagePicked
//
//        val picker = UIImagePickerController()
//        picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary
//        picker.delegate = this
//        picker.allowsEditing = false
//
//        val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
//        controller?.presentViewController(picker, animated = true, completion = null)
//    }
//
//    @ObjCAction
//    fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>?) {
//        val image = didFinishPickingMediaWithInfo?.get(UIImagePickerControllerOriginalImage) as? UIImage
//        // TODO: Сохрани в файл или temp и верни путь
//        val path = "ios_photo_path_placeholder"
//        callback?.invoke(path)
//
//        picker.dismissViewControllerAnimated(true, completion = null)
//    }
//
//    @ObjCAction
//    fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
//        picker.dismissViewControllerAnimated(true, completion = null)
//    }
//}
