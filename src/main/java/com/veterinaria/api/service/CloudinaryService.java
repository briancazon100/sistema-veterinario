package com.veterinaria.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.veterinaria.api.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service


public class CloudinaryService {

    private static final List<String> TIPOS_IMAGEN_PERMITIDOS = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final List<String> EXTENSIONES_IMAGEN_PERMITIDAS = List.of(
            ".jpg",
            ".jpeg",
            ".png",
            ".webp"
    );

    private static final long MAX_BYTES_IMAGEN = 5 * 1024 * 1024; // 5 MB

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String subirArchivo(MultipartFile archivo) throws IOException {
        String nombreOriginal = limpiarNombreArchivo(archivo.getOriginalFilename());

        Map<String, Object> opciones = ObjectUtils.asMap(
                "resource_type", "auto",
                "use_filename", true,
                "unique_filename", true,
                "filename_override", nombreOriginal
        );

        Map<?, ?> subida = cloudinary.uploader().upload(
                archivo.getBytes(),
                opciones
        );

        Object secureUrl = subida.get("secure_url");

        if (secureUrl == null) {
            throw new IOException("Cloudinary no devolvió una URL segura.");
        }

        return secureUrl.toString();
    }

    public String subirImagenMascota(MultipartFile archivo, Long veterinariaId) throws IOException {
        validarImagen(archivo);

        String nombreOriginal = limpiarNombreArchivo(archivo.getOriginalFilename());
        String carpeta = (veterinariaId != null)
                ? "veterinarias/vet_" + veterinariaId + "/mascotas"
                : "mascotas";

        Map<String, Object> opciones = ObjectUtils.asMap(
                "resource_type", "image",
                "folder", carpeta,
                "use_filename", true,
                "unique_filename", true,
                "filename_override", nombreOriginal
        );

        Map<?, ?> subida = cloudinary.uploader().upload(
                archivo.getBytes(),
                opciones
        );

        Object secureUrl = subida.get("secure_url");

        if (secureUrl == null) {
            throw new IOException("Cloudinary no devolvió una URL segura para la imagen.");
        }

        return secureUrl.toString();
    }

    private void validarImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("Debes seleccionar una imagen para la mascota.");
        }

        if (archivo.getSize() > MAX_BYTES_IMAGEN) {
            throw new BadRequestException("La imagen no debe superar los 5 MB.");
        }

        String contentType = archivo.getContentType();
        if (contentType == null || !TIPOS_IMAGEN_PERMITIDOS.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Formato no permitido. Solo se aceptan imágenes JPG, PNG o WEBP.");
        }

        String nombreMinuscula = (archivo.getOriginalFilename() != null)
                ? archivo.getOriginalFilename().toLowerCase()
                : "";

        boolean extensionValida = EXTENSIONES_IMAGEN_PERMITIDAS.stream()
                .anyMatch(nombreMinuscula::endsWith);

        if (!extensionValida) {
            throw new BadRequestException("La extensión del archivo debe ser .jpg, .jpeg, .png o .webp.");
        }
    }

    private String limpiarNombreArchivo(String nombreOriginal) {
        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            return "archivo";
        }
        String limpio = nombreOriginal.replace("\\", "/");
        return limpio.substring(limpio.lastIndexOf("/") + 1);
    }
}