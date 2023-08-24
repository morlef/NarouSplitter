package si.f5.luna3419.narou.split;

import org.jetbrains.annotations.Nullable;

import java.io.File;

public record FileSet(String nCode, File main, @Nullable File impression, @Nullable File review) {
}
