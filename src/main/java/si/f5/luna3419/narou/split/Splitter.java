package si.f5.luna3419.narou.split;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class Splitter {
    private static final String splitter = "------------------------- ";
    private final File output;
    private final FileSet files;

    public void convert() {
        NovelData novelData = new NovelData();
        File out = new File(output, files.nCode());
        Main.getLogger().info(splitter);
        Main.getLogger().info("Converting novel data...");
        if (out.mkdir()) {
            Main.getLogger().info("- Create " + files.nCode() + " output directory.");
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(files.main().getPath()), StandardCharsets.UTF_8);
            int line = 0;

            for (; !lines.get(line).startsWith(splitter); line++) {
                switch (lines.get(line)) {
                    case "【ユーザ情報】" -> {
                        novelData.setUserId(lines.get(line + 1).split(" ")[1]);
                        novelData.setUserName(lines.get(line + 2).split(" ")[1]);
                    }
                    case "【Nコード】" -> novelData.setNCode(lines.get(line + 1));
                    case "【タイトル】" -> novelData.setTitle(lines.get(line + 1));
                    case "【作者名】" -> novelData.setAuthor(lines.get(line + 1));
                    case "【種別】" -> novelData.setType(lines.get(line + 1));
                    case "【完結設定】" -> novelData.setCompletion(lines.get(line + 1));
                    case "【年齢制限】" -> novelData.setAgeLimit(lines.get(line + 1));
                    case "【ジャンル】" -> novelData.setGenre(lines.get(line + 1));
                    case "【登録必須キーワード】" -> novelData.setRequiredKeyword(Arrays.asList(lines.get(line + 1).split(" ")));
                    case "【キーワード】" -> novelData.setKeyword(Arrays.asList(lines.get(line + 1).split(" ")));
                    case "【あらすじ】" -> {
                        for (int i = 1; !lines.get(line + i).equals("【掲載部分数】"); i++) {
                            novelData.getSynopsis().add(lines.get(line + i));
                        }
                    }
                    case "【掲載部分数】" -> novelData.setPublications(Integer.parseInt(lines.get(line + 1)));
                    case "【初回掲載日時】" -> novelData.setFirstTime(lines.get(line + 1));
                    case "【最終掲載日時】" -> novelData.setLastTime(lines.get(line + 1));
                    case "【感想受付】" -> novelData.setImpressionReception(lines.get(line + 1));
                    case "【レビュー受付】" -> novelData.setReviewReception(lines.get(line + 1));
                    case "【評価受付】" -> novelData.setEvaluationReception(lines.get(line + 1));
                    case "【いいね受付】" -> novelData.setNiceReception(lines.get(line + 1));
                    case "【開示設定】" -> novelData.setDisclosure(lines.get(line + 1));
                    case "【評価】" -> {
                        novelData.setTotalEvaluationPoints(Integer.parseInt(lines.get(line + 1).split(" ")[1].replace("pt", "")));
                        novelData.setTotalEvaluationNumbers(Integer.parseInt(lines.get(line + 2).split(" ")[1].replace("人", "")));
                        novelData.setFavorites(Integer.parseInt(lines.get(line + 3).split(" ")[1].replace("件", "")));
                        novelData.setEvaluationPoints(Integer.parseInt(lines.get(line + 4).split(" ")[1].replace("pt", "")));
                        novelData.setEvaluationAverage(Double.parseDouble(lines.get(line + 5).split(" ")[1].replace("pt", "")));
                    }
                    case "【いいね】" -> {
                        if (lines.get(line + 1).replace("件", "").equals("-")) {
                            novelData.setNice(0);
                        } else {
                            novelData.setNice(Integer.parseInt(lines.get(line + 1).replace("件", "")));
                        }
                    }
                }
            }
            line++;

            Main.getLogger().info(splitter);
            Main.getLogger().info("Splitting " + novelData.getTitle() + " episode data...");
            split(novelData, files.main(), line, new File(out, novelData.getTitle()));
            novelData.dump(new File(out, "data.yml"));

            if (files.impression() != null) {
                Main.getLogger().info(splitter);
                Main.getLogger().info("Splitting " + novelData.getTitle() + " impression data...");
                split(novelData, files.impression(), 1, new File(out, "感想"));
            }
            if (files.review() != null) {
                Main.getLogger().info(splitter);
                Main.getLogger().info("Splitting " + novelData.getTitle() + " review data...");
                split(novelData, files.review(), 1, new File(out, "レビュー"));
            }
        } catch (IOException e) {
            Main.getLogger().info(e.getMessage());
        }
    }

    private void split(NovelData data, File input, int line, File output) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(input.getPath()), StandardCharsets.UTF_8);
        List<String> tempLines = new ArrayList<>();

        if (output.mkdir()) {
            Main.getLogger().info("- Create " + data.getTitle() + " output directory. (directory name: " + output.getName() + ")");
            Main.getLogger().info(splitter);
        }

        String title = "";
        for (int count = 1;; count++) {
            for (; !(lines.get(line).startsWith(splitter) || lines.get(line).equals("【免責事項】")); line++) {
                if (lines.get(line).startsWith("【第")) {
                    data.getChapterData().add(count + "~ " + lines.get(line + 1));
                    line += 2;
                    continue;
                }
                if (lines.get(line).equals("【サブタイトル】")) {
                    title = String.format("%-4d", count) + " - " +lines.get(line + 1);
                    line += 2;
                    continue;
                }
                if (lines.get(line).startsWith("【投稿者情報】")) {
                    if (lines.get(line + 2).contains(":")) {
                        title = String.format("%-3s", count) + " - " + lines.get(line + 2).split(" ")[1];
                    } else {
                        title = String.format("%-3s", count) + " - " + lines.get(line + 1).split(" ")[1];
                    }
                }
                tempLines.add(lines.get(line));
            }
            title = Main.replaceIllegalCharacter(title);

            Main.getLogger().info(title);
            Files.write(new File(output, title + ".txt").toPath(), tempLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            tempLines.clear();
            if (lines.get(line).equals("【免責事項】")) {
                break;
            }
            line++;
        }
    }
}
