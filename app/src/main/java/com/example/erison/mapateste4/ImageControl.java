package com.example.erison.mapateste4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Base64;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Controla as imagens no site e ajuda no upload de imagens
 */

public class ImageControl {
    /**
     * Upload image on the serve by recieved an image Uri
     * # can be deleted
     * @param imgUri
     * @param imageName
     */
    public static void upload(Uri imgUri, String imageName, Context ctx) {
        // Image
        InputStream image_stream = null;
        try {
            if(imgUri == null){
                System.err.println("tá vazio não era pra estar");
            }
            System.out.println("tô aqui");
            System.out.println(imgUri.getPath().toString());
            image_stream = ctx.getContentResolver().openInputStream(imgUri);
            Bitmap bm = BitmapFactory.decodeStream(image_stream);
            upload(bm,imageName, ctx);
        } catch (FileNotFoundException e) {
            System.err.println("Não deu certo");
            e.printStackTrace();
        }

    }

    /**
     * Upload an image to server by using  an bitmap
     * @param bm
     * @param imageName
     */
    public static void upload(Bitmap bm, String imageName, Context ctx) {
        //turn to bytearray
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm = Bitmap.createScaledBitmap(bm,
                700,(int)(700f*((float)bm.getHeight()/(float)bm.getWidth())),false);
        bm.compress(Bitmap.CompressFormat.JPEG, 40, bao);
        byte[] ba = bao.toByteArray();
        //enncod the byte to string, so we can send to the server
        String byteString = Base64.encodeToString(ba,Base64.DEFAULT);

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);System.out.println(s);System.out.println(s);System.out.println(s);
            }
        };
        // Upload image to server
        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/images/upload_image.php", byteString, imageName, response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

}
