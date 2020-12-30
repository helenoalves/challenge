/* (C)2020 */
package org.challenge.fileimport;

public class FileImportException extends RuntimeException {

  public FileImportException(String message) {
    super(message);
  }

  public FileImportException(String message, Exception catched) {
    super(message, catched);
  }
}
