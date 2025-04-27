package com.example.profile

//suspend fun uploadPhotoToSupabase(localResourceName: String, bucketName: String): String? {
//    val storage = supabaseClient.storage.from(bucketName)
//
//    // Получаем байты изображения
//    val bytes = getBytesFromResource(localResourceName)
//    if (bytes == null) return null
//
//    // Генерация уникального имени для файла
//    val fileName = "photos/${UUID.randomUUID()}.jpg"
//
//    // Загружаем файл в Supabase Storage
//    val result = withContext(Dispatchers.IO) {
//        storage.upload(path = fileName, file = bytes, options = { options ->
//            options.cacheControl = "3600"  // Параметры кеширования
//            options.upsert = false         // Не перезаписываем существующие файлы
//        })
//    }
//
//    // Если загрузка успешна, возвращаем публичный URL
//    return if (result is FileUploadResponse.Success) {
//        // Получаем публичный URL
//        val publicUrl = storage.getPublicUrl(fileName)
//        publicUrl // возвращаем ссылку
//    } else {
//        null // если загрузка не удалась, возвращаем null
//    }
//}
//
//// Получение байтов из ресурса
//fun getBytesFromResource(resourceName: String): ByteArray? {
//    return try {
//        val inputStream = javaClass.classLoader?.getResourceAsStream("assets/$resourceName")
//        inputStream?.readBytes()
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
//}