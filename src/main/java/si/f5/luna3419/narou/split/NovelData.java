package si.f5.luna3419.narou.split;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NovelData {
    private String userId;
    private String userName;

    private String nCode;
    private String title;
    private String author;

    private String type;
    private String completion;
    private String ageLimit;

    private String genre;
    private List<String> requiredKeyword;
    private List<String> keyword;
    private List<String> synopsis = new ArrayList<>();

    private int publications;
    private int nice;
    private String firstTime;
    private String lastTime;
    private String impressionReception;
    private String reviewReception;
    private String evaluationReception;
    private String niceReception;
    private String disclosure;

    private int totalEvaluationPoints;
    private int totalEvaluationNumbers;
    private int favorites;
    private int evaluationPoints;
    private double evaluationAverage;

    private List<String> chapterData = new ArrayList<>();

    public void dump(File output) {
        try (FileWriter writer = new FileWriter(output)) {
            new Yaml().dump(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
