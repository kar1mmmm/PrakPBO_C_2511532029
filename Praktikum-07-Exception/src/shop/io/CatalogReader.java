package shop.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import shop.exception.CatalogLoadException;
import shop.model.PhysicalProduct;
import shop.model.Product;

public class CatalogReader {
    private static final Logger LOG = Logger.getLogger("shop");
    private int skippedCount;

    public int getSkippedCount() { return skippedCount; }

    public List<Product> load(Path file) throws CatalogLoadException {
        List<Product> products = new ArrayList<>();
        skippedCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    products.add(parse(line));
                } catch (IllegalArgumentException e) {
                    skippedCount++;
                    LOG.warning("Baris " + lineNumber + " dilewati: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new CatalogLoadException("Katalog gagal dibaca: " + file, e);
        }
        return products;
    }

    private Product parse(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("expected 4 fields, found " + fields.length);
        }
        return new PhysicalProduct(fields[0].trim(), fields[1].trim(),
                Long.parseLong(fields[2].trim()), Integer.parseInt(fields[3].trim()));
    }
}
