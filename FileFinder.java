import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileFinder {
    Path pathToSearchIn;
    File directoryToSearch;

    public FileFinder(Path pathToSearchIn) {
        this.pathToSearchIn = pathToSearchIn;
        this.directoryToSearch = pathToSearchIn.toFile();
    }

    public boolean checkExistenceOf(String pathName) {
        String directoryPath = pathToSearchIn.toString();
        File fileToBeSearched = new File(directoryPath + "\\" + pathName);
        return Files.exists(fileToBeSearched.toPath().toAbsolutePath());
    }

    public File getFile(String fileName) {
        File file = Objects.requireNonNull(directoryToSearch.listFiles((dir, name) -> name.equals(fileName)))[0];

        return file;
    }
}
