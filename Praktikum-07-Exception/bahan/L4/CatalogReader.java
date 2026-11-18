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

/** Reads lines of the form: sku;name;price;weightGrams */
public class CatalogReader {
    private static final Logger LOG = Logger.getLogger(CatalogReader.class.getName());

    private int skippedCount;

    public List<Product> load(Path file) throws CatalogLoadException {
        List<Product> products = new ArrayList<>();
        skippedCount = 0;
        // TODO L4.b
        // 1. Open the file with try-with-resources: BufferedReader reader = Files.newBufferedReader(file)
        // 2. Read line by line (reader.readLine() returns null at the end) and count the line number.
        // 3. Parse each line inside its OWN try-catch (IllegalArgumentException):
        //    a broken line -> skippedCount++, LOG.warning("Baris <n> dilewati: <message>"), continue.
        // 4. IOException -> throw new CatalogLoadException("Katalog gagal dibaca: " + file, e)
        return products;
    }

    /** Number of broken lines skipped by the last load(...). */
    public int getSkippedCount() {
        return skippedCount;
    }

    private Product parse(String line) {
        String[] fields = line.split(";");
        if (fields.length != 4) {
            throw new IllegalArgumentException("expected 4 fields, found " + fields.length);
        }
        return new PhysicalProduct(fields[0].trim(), fields[1].trim(),
                Long.parseLong(fields[2].trim()), Integer.parseInt(fields[3].trim()));
    }
}
