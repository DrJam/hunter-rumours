package com.drjam.rumourreminder;

import java.awt.*;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

class RumourReminderOverlay extends OverlayPanel {

    private final Client client;
    private final RumourReminderConfig config;
    private final RumourReminderPlugin plugin;

    @Inject
    private RumourReminderOverlay(Client client, RumourReminderConfig config, RumourReminderPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        this.config = config;

        setPosition(OverlayPosition.TOP_CENTER);
        setPriority(0.5f);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (!this.config.whistleWarning() || this.plugin.hasEnoughWhistleCharges) {
            return super.render(graphics);
        }

        if (!this.plugin.hasEnoughWhistleCharges) {

            final String text = "Recharge Quetzal Whistle!";
            final Color color = config.whistleWarningTextColor();
            final float size = (float) config.whistleWarningTextSize();
            final Font font = graphics.getFont().deriveFont(size);

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(text)
                    .leftFont(font)
                    .leftColor(color)
                    .build());
        }
        return super.render(graphics);
    }
}