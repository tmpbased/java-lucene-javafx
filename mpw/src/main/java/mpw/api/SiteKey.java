package mpw.api;

import mpw.template.Template;

public interface SiteKey {
  String format(Template template);

  String format(Renderer<? extends Template> templateGen);
}
