package edu.eci.arsw.blueprints.controllers;

/**
 * Standard API Response wrapper.
 * 
 * @param code    HTTP Status Code
 * @param message Description of the result
 * @param data    Payload data (can be null)
 * @param <T>     Type of the data
 */
public record ApiResponse<T>(int code, String message, T data) {
}
