/*
 * Copyright (c) 2020, TheStonedTurtle <https://github.com/TheStonedTurtle>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package thestonedturtle.partypanel;

import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import thestonedturtle.partypanel.data.PartyPlayer;
import thestonedturtle.partypanel.ui.PlayerBanner;
import thestonedturtle.partypanel.ui.PlayerPanel;

class PartyPanel extends PluginPanel
{
	private static final Color BACKGROUND_COLOR = ColorScheme.DARK_GRAY_COLOR;
	private static final Color BACKGROUND_HOVER_COLOR = ColorScheme.DARK_GRAY_HOVER_COLOR;

	private final PartyPanelPlugin plugin;
	private final HashMap<UUID, PlayerPanel> playerPanelMap = new HashMap<>();
	private final JPanel basePanel;

	@Inject
	PartyPanel(final PartyPanelPlugin plugin)
	{
		super(false);
		this.plugin = plugin;
		this.setLayout(new BorderLayout());

		basePanel = new JPanel();
		basePanel.setBorder(new EmptyBorder(BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET));
		basePanel.setLayout(new DynamicGridLayout(0, 1, 0, 3));

		// Wrap content to anchor to top and prevent expansion
		final JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(basePanel, BorderLayout.NORTH);
		final JScrollPane scrollPane = new JScrollPane(northPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		this.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Shows all members of the party, excluding the local player, in banner view. See {@link PlayerBanner)
	 */
	void renderSidebar()
	{
		basePanel.removeAll();
//		System.out.print("Inside renderSidebar, party members are: ");
//		plugin.getPartyMembers().forEach((k, v) -> {
//			System.out.printf("%s---'%s' ", v.getMemberId(), v.getUsername());
//		});
//		System.out.println();

		// Sort by their RSN first; If it doesn't exist sort by their Discord name instead
		final List<PartyPlayer> players = plugin.getPartyMembers().values()
			.stream()
			.sorted(Comparator.comparing(o -> o.getUsername() == null ? o.getMember().getName() : o.getUsername()))
			.collect(Collectors.toList());

		for (final PartyPlayer player : players)
		{

//			System.out.println("Adding " + player.getUsername() + " panel");
			drawPlayerPanel(player);

//			System.out.print("Now panelMap contains:");
//			playerPanelMap.forEach((k, v) -> {
//				System.out.printf("%s---'%s' ", v.getPlayer().getMemberId(), v.getPlayer().getUsername());
//			});
//			System.out.println();

			if (player != players.get(players.size()-1))
			{
				System.out.printf("player '%s' != '%s'\n", player.getUsername(), players.get(players.size()-1).getUsername());
				System.out.printf("player '%s' != '%s'\n", player.getMember().getName(), players.get(players.size()-1).getMember().getName());

				final JPanel spacer = new JPanel();
				spacer.setBorder(new EmptyBorder(0, 0, 4, 0));
				basePanel.add(spacer);
			}
		}

		if (getComponentCount() == 0)
		{
			basePanel.add(new JLabel("There are no members in your party"));
		}

		basePanel.revalidate();
		basePanel.repaint();
		System.out.println("End renderSidebar\n");
	}

	void drawPlayerPanel(PartyPlayer player)
	{
		System.out.println("Drawing " + player.getUsername() + " player panel");
		PlayerPanel playerPanel = playerPanelMap.get(player.getMemberId());

		if (playerPanel != null)
		{
			playerPanel.changePlayer(player);
		}
		else
		{
			playerPanelMap.put(player.getMemberId(), new PlayerPanel(player, plugin.spriteManager, plugin.itemManager));
		}

		basePanel.add(playerPanelMap.get(player.getMemberId()));
		basePanel.revalidate();
		basePanel.repaint();
	}

	void removePartyPlayer(final PartyPlayer player)
	{

		System.out.print("Inside removePartyPlayer, player is ");
		System.out.println(player==null?"null":"not null");
		if (player != null)
		{
			System.out.printf("Removing player '%s'\n", player.getUsername());
			playerPanelMap.remove(player.getMemberId());

			renderSidebar();
		}

		System.out.println("End removePartyPlayer\n");
	}
}
