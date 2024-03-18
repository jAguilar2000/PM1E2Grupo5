package com.example.pm1e2grupo5.Infrastructure.ImageCompresors;

import android.graphics.Bitmap;
import android.util.Base64;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;
//
public class SignatureExtractor {


    public String convertSignatureToBase64(SignaturePad firma) {
        boolean isEmpty = firma.isEmpty();

        if(isEmpty) return "off";

        Bitmap signatureBitmap = firma.getSignatureBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
