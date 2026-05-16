package com.euclid.ota;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class AnimatedBackground extends View {
    private Paint paint;
    private ArrayList<Particle> particles;
    private Random random;
    private final int PARTICLE_COUNT = 35;

    public AnimatedBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        particles = new ArrayList<>();
        random = new Random();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        particles.clear();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle(random.nextInt(w), random.nextInt(h)));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw glowing connection lines (Geometric network look)
        paint.setStrokeWidth(2f);
        for (int i = 0; i < particles.size(); i++) {
            Particle p1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle p2 = particles.get(j);
                float dx = p1.x - p2.x;
                float dy = p1.y - p2.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < 250) {
                    // Make lines brighter when nodes are closer
                    int alpha = (int) (255 * (1.0f - (distance / 250f))) / 4;
                    paint.setColor(0x3A86FF); // Euclid Blue
                    paint.setAlpha(alpha);
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                }
            }
        }

        // Move and draw individual particles
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x3A86FF);
        for (Particle p : particles) {
            p.move(getWidth(), getHeight());
            paint.setAlpha(p.alpha);
            canvas.drawCircle(p.x, p.y, p.radius, paint);
        }

        // Loop the animation fluidly
        invalidate();
    }

    private class Particle {
        float x, y;
        float vx, vy;
        float radius;
        int alpha;

        Particle(float x, float y) {
            this.x = x;
            this.y = y;
            // Slow, sleek geometric drift
            this.vx = (random.nextFloat() - 0.5f) * 1.5f;
            this.vy = (random.nextFloat() - 0.5f) * 1.5f;
            this.radius = random.nextFloat() * 4f + 2f;
            this.alpha = random.nextInt(150) + 50;
        }

        void move(int width, int height) {
            x += vx;
            y += vy;

            // Bounce smoothly off the edges of the display
            if (x < 0 || x > width) vx *= -1;
            if (y < 0 || y > height) vy *= -1;
        }
    }
}
