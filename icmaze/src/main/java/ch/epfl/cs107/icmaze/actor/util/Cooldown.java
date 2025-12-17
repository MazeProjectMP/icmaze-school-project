package ch.epfl.cs107.icmaze.actor.util;
/**
 * Utility class representing a time-based cooldown mechanism.
 * <p>
 * A {@code Cooldown} accumulates elapsed time until a specified cooldown duration
 * is reached. Once ready, it resets automatically and signals that the cooldown
 * has completed. This class is typically used to limit the frequency of actions
 * such as attacks, movements, or state transitions.
 * </p>
 */
public final class Cooldown {
  private float t = 0f;
  private float cd;

    /**
     * Creates a new cooldown with the given duration.
     * @param cd (float): cooldown duration in seconds
     */
  public Cooldown(float cd) {
    this.cd = cd;
  }

    /**
     * Updates the cooldown timer and checks whether the cooldown is complete.
     * The elapsed time {@code dt} is accumulated internally. If the accumulated
     * time reaches or exceeds the cooldown duration, the timer is reset and
     * this method returns {@code true}.
     * @param dt (float): time elapsed since the last update
     * @return (boolean): {@code true} if the cooldown has completed, {@code false} otherwise
     */
  public boolean ready(float dt) {
    t += dt;
    if (t >= cd) { t = 0f; return true; }
    return false;
  }
    /**
     * Resets the cooldown timer to zero.
     * After calling this method, the cooldown must elapse again before
     * {@link #ready(float)} can return {@code true}.
     */
  public void reset() {
    t = 0f;
  }
}
