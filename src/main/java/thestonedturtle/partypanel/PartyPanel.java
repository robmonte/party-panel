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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
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
	//private final Map<UUID, PlayerBanner> bannerMap = new HashMap<>();
	private final Map<UUID, PlayerPanel> panelMap = new HashMap<>();
	private final JPanel panel;

	@Inject
	PartyPanel(final PartyPanelPlugin plugin)
	{
		super(false);
		this.plugin = plugin;
		this.setLayout(new BorderLayout());

		panel = new JPanel();
		panel.setBorder(new EmptyBorder(BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET));
		panel.setLayout(new DynamicGridLayout(0, 1, 0, 3));

		// Wrap content to anchor to top and prevent expansion
		final JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(panel, BorderLayout.NORTH);
		final JScrollPane scrollPane = new JScrollPane(northPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		this.add(scrollPane, BorderLayout.CENTER);
		//this.add(createLeaveButton(), BorderLayout.SOUTH);
	}

	void refreshUI()
	{
		System.out.println("Inside refreshUI");
		//panel.removeAll();
//		if (selectedPlayer == null)
//		{
//			showBannerView();
//		}
//		else if (plugin.getPartyMembers().containsKey(selectedPlayer.getMemberId()))
//		{
//			showPlayerView();
//		}
//		else
//		{
//			selectedPlayer = null;
//			showBannerView();
//		}
		renderSidebar();
	}

	/**
	 * Shows all members of the party, excluding the local player, in banner view. See {@link PlayerBanner)
	 */
	void renderSidebar()
	{
		System.out.print("Inside renderSidebar, party members are: ");
		plugin.getPartyMembers().forEach((k, v) -> {
			System.out.printf("%s---'%s' ", v.getMemberId(), v.getUsername());
		});
		System.out.println();
		//selectedPlayer = null;
		//panel.removeAll();

		final Collection<PartyPlayer> players = plugin.getPartyMembers().values()
			.stream()
			// Sort by username, if it doesn't exist use their discord name
			.sorted(Comparator.comparing(o -> o.getUsername() == null ? o.getMember().getName() : o.getUsername()))
			.collect(Collectors.toList());

		for (final PartyPlayer player : players)
		{
//			banner.addMouseListener(new MouseAdapter()
//			{
//				@Override
//				public void mousePressed(MouseEvent e)
//				{
//					if (e.getButton() == MouseEvent.BUTTON1)
//					{
//						//selectedPlayer = player;
//						showPlayerView(player);
//					}
//				}
//
//				@Override
//				public void mouseEntered(MouseEvent e)
//				{
//					banner.setBackground(BACKGROUND_HOVER_COLOR);
//				}
//
//				@Override
//				public void mouseExited(MouseEvent e)
//				{
//					banner.setBackground(BACKGROUND_COLOR);
//				}
//			});

			//System.out.print("Now bannerMap contains:");
			//bannerMap.forEach((k, v) -> {
			//	System.out.printf("%s---'%s' ", v.getPlayer().getMemberId(), v.getPlayer().getUsername());
			//});
			//System.out.println();

			System.out.println("Adding " + player.getUsername() + " panel");
			drawPlayerPanel(player);

			System.out.print("Now panelMap contains:");
			panelMap.forEach((k, v) -> {
				System.out.printf("%s---'%s' ", v.getPlayer().getMemberId(), v.getPlayer().getUsername());
			});
			System.out.println();

		}

		if (getComponentCount() == 0)
		{
			panel.add(new JLabel("There are no members in your party"));
		}

		panel.revalidate();
		panel.repaint();
		System.out.println("End renderSidebar\n");
	}

	void drawPlayerPanel(PartyPlayer player)
	{
//		if (selectedPlayer == null)
//		{
//			drawPanel();
//		}

		//panel.removeAll();
		//panel.add(createReturnButton());

		if (panelMap.get(player.getMemberId()) != null)
		{
			panelMap.get(player.getMemberId()).changePlayer(player);
			//playerPanel.changePlayer(selectedPlayer);
		}
		else
		{
			panelMap.put(player.getMemberId(), new PlayerPanel(player, plugin.spriteManager, plugin.itemManager));
			//playerPanel = new PlayerPanel(selectedPlayer, plugin.spriteManager, plugin.itemManager);
		}

		panel.add(panelMap.get(player.getMemberId()));
		panel.revalidate();
		panel.repaint();
	}

	private JButton createReturnButton()
	{
		final JButton label = new JButton("Return to party overview");
		label.setFocusable(false);
		label.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				label.setBackground(BACKGROUND_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				label.setBackground(BACKGROUND_COLOR);
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					renderSidebar();
				}
			}
		});

		return label;
	}

	private JButton createLeaveButton()
	{
		final JButton label = new JButton("Leave Party");
		label.setFocusable(false);
		label.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				label.setBackground(BACKGROUND_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				label.setBackground(BACKGROUND_COLOR);
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					//selectedPlayer = null;
					//bannerMap.clear();
					//playerPanel = null;
					panelMap.clear();
					plugin.leaveParty();
				}
			}
		});

		return label;
	}

	void updatePartyPlayer(final PartyPlayer player)
	{
		System.out.println("Inside updatePartyPlayer");
		//if (selectedPlayer == null)
		//if (player == null)
		//{
			//System.out.println("++++++++++   PLAYER WAS NULL   ++++++++++");
			//PlayerBanner banner = bannerMap.get(player.getMemberId());
			//if (banner == null)
			//{
			//	System.out.println("banner was null");
				// New member, recreate entire view
			//	renderSidebar();
			//	return;
			//}

			//final String oldPlayerName = banner.getPlayer().getUsername();
			//banner.setPlayer(player);
			//if (!Objects.equals(player.getUsername(), oldPlayerName))
			//{
			//	banner.recreatePanel();
			//}
			//else
			//{
			//	banner.refreshStats();
			//}
		//}
		//else
		if (player != null)
		{
			System.out.println("player was not null");
			//if (player.getMemberId().equals(selectedPlayer.getMemberId()))
			//{
				//this.selectedPlayer = player;
				//showPlayerView(player);
			renderSidebar();
			//}
		}

		System.out.println("End updatePartyPlayer\n");
	}

	void removePartyPlayer(final PartyPlayer player)
	{

		System.out.print("Inside removePartyPlayer, player is ");
		System.out.println(player==null?"null":"not null");
		//if (selectedPlayer != null && !selectedPlayer.getMemberId().equals(player.getMemberId()))
		if (player == null)
		{
			return;
		}

		//selectedPlayer = null;

		System.out.printf("Removing player '%s'\n", player.getUsername());
		//bannerMap.remove(player.getMemberId());
		panelMap.remove(player.getMemberId());

		panel.removeAll();
		renderSidebar();
		System.out.println("End removePartyPlayer\n");
	}
}
