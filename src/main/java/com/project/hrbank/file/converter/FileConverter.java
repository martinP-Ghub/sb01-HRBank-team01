package com.project.hrbank.file.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public class FileConverter {
	public static byte[] convertToUtf8(MultipartFile file, Path filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(
			new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
			 BufferedWriter writer = new BufferedWriter(
				 new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
		}
		return Files.readAllBytes(filePath);

	}

}
