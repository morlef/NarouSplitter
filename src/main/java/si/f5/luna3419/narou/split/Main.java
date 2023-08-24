package si.f5.luna3419.narou.split;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    @Getter
    private static final Logger logger = LoggerFactory.getLogger("Narou");

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        List<FileSet> novelFiles = new ArrayList<>();

        File dir = new File(System.getProperty("java.class.path")).getAbsoluteFile().getParentFile();
        File input = new File(dir, "input");
        File output = new File(dir, "output");

        if (output.mkdir()) {
            logger.info("Create output directory.");
        }
        if (input.mkdir()) {
            logger.info("Create input directory.");
        }

        File[] files = input.listFiles((file) -> file.getName().matches("N[0-9]{4}[A-Z]{2}\\.txt"));
        assert files != null;

        if (files.length == 0) {
            logger.info("Input directory is empty.");
            logger.info("Stopping.");
            System.exit(0);
        }

        logger.info("-------------------------");
        logger.info("Checking files...");
        for (File file : files) {
            String nCode = file.getName().substring(0,file.getName().lastIndexOf('.'));
            logger.info(nCode);

            File impression = new File(file.getParent(), nCode + "_impression.txt");
            File review = new File(file.getParent(), nCode + "_review.txt");

            novelFiles.add(new FileSet(nCode, file, impression.exists() ? impression : null, review.exists() ? review : null));
        }

        logger.info("-------------------------");
        logger.info("Start splitting...");
        novelFiles.forEach(file -> new Splitter(output, file).convert());
    }

    public static String replaceIllegalCharacter(String str) {
        return str.replace("\\", "￥").replace("/", "／").replace(":", "：")
                .replace("*", "＊").replace("?", "？").replace("\"", "”")
                .replace("<", "＜").replace(">", "＞").replace("|", "｜");
    }
}