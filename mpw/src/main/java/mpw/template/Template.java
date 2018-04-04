package mpw.template;

import java.util.List;

import mpw.api.Renderer;
import mpw.letter.Letter;

public interface Template extends List<Letter>, Renderer<String> {
}
