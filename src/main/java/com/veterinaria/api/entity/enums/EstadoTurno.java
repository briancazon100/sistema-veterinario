package com.veterinaria.api.entity.enums;

public enum EstadoTurno {
    PENDIENTE,// El cliente reservó pero aún no llegó
    ATENDIDO,//El veterinario ya lo revisó
    CANCELADO//El cliente avisó que no va, o no apareció
}
