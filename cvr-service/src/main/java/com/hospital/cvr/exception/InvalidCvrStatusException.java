/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.exception;

/**
 *
 * @author mduhijod
 */

public class InvalidCvrStatusException extends RuntimeException {
    public InvalidCvrStatusException(String message) {
        super(message);
    }
}
