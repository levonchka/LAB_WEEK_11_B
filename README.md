<img width="317" height="686" alt="image" src="https://github.com/user-attachments/assets/126d34de-a27e-4236-9588-fb6d62a20990" />


<img width="315" height="677" alt="image" src="https://github.com/user-attachments/assets/39e721cb-c60c-4745-a0d2-3ca4e6f7fcd4" />


<img width="291" height="654" alt="image" src="https://github.com/user-attachments/assets/5bb6775c-2188-4950-8f33-80ce8fdafeb0" />


Assignment
Soal 1.	 
Kode yang menangani penyimpanan file hasil foto ke path berdasarkan URI adalah saat TakePictureLauncher dijalankan dan ketika file dimasukkan ke MediaStore melalui insertImageToStore():

Kode:
takePictureLauncher =
    registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            providerFileManager.insertImageToStore(photoInfo)
        }
    }

Dan proses penyimpanan sebenarnya dilakukan di:

val input = contentResolver.openInputStream(fileInfo.uri)
val output = contentResolver.openOutputStream(insertedUri)
IOUtils.copy(input, output)




Soal 2. 
•	URI (atribut pertama)
URI adalah alamat aman (content URI) yang diberikan oleh FileProvider. URI ini menunjuk ke lokasi file yang akan diisi oleh kamera ketika foto diambil.
•	relativePath (atribut keempat)
relativePath adalah folder tujuan di MediaStore tempat file akan disimpan, misalnya “Pictures/” atau “Movies/”, yang menentukan lokasi penyimpanan publik di galeri.

Soal 3.  
1.	User menekan tombol untuk mengambil foto.
2.	Aplikasi membuat file kosong dan URI menggunakan FileProvider (generatePhotoUri).
3.	Kamera dibuka dan menulis hasil foto ke URI tersebut.
4.	Setelah foto berhasil, insertImageToStore dipanggil.
5.	File dibaca dari URI lalu disalin ke MediaStore menggunakan ContentResolver.
6.	Foto muncul di galeri karena sudah tersimpan sebagai media publik.

