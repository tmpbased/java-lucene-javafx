package tilt.impl.lucene;

import com.google.auto.service.AutoService;

import tilt.api.Tilt;
import tilt.api.TiltService;
import tilt.lib.receiver.Receiver;

@AutoService({ TiltService.class })
public final class LuceneService implements TiltService {
  @Override
  public Tilt tilt(final Receiver receiver) {
    return new Lucene(receiver);
  }
}
