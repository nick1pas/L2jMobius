/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets.revenge;

import java.util.Collection;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.instancemanager.RevengeHistoryManager;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.RevengeHistoryHolder;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Mobius
 */
public class ExPvpBookShareRevengeList implements IClientOutgoingPacket
{
	private final Collection<RevengeHistoryHolder> _history;
	
	public ExPvpBookShareRevengeList(Player player)
	{
		_history = RevengeHistoryManager.getInstance().getHistory(player);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PVPBOOK_SHARE_REVENGE_LIST.writeId(packet);
		if (_history == null)
		{
			packet.writeC(1); // CurrentPage
			packet.writeC(1); // MaxPage
			packet.writeD(0);
		}
		else
		{
			packet.writeC(1); // CurrentPage
			packet.writeC(1); // MaxPage
			packet.writeD(_history.size());
			for (RevengeHistoryHolder holder : _history)
			{
				packet.writeD(holder.getType().ordinal()); // ShareType (2 - help request, 1 - revenge, 0 - both)
				packet.writeD((int) (holder.getKillTime() / 1000)); // KilledTime
				packet.writeD(holder.getShowLocationRemaining()); // ShowKillerCount
				packet.writeD(holder.getTeleportRemaining()); // TeleportKillerCount
				packet.writeD(holder.getSharedTeleportRemaining()); // SharedTeleportKillerCount
				packet.writeD(0); // KilledUserDBID
				packet.writeString(holder.getVictimName()); // KilledUserName
				packet.writeString(holder.getVictimClanName()); // KilledUserPledgeName
				packet.writeD(holder.getVictimLevel()); // KilledUserLevel
				packet.writeD(holder.getVictimRaceId()); // KilledUserRace
				packet.writeD(holder.getVictimClassId()); // KilledUserClass
				packet.writeD(0); // KillUserDBID
				packet.writeString(holder.getKillerName()); // KillUserName
				packet.writeString(holder.getKillerClanName()); // KillUserPledgeName
				packet.writeD(holder.getKillerLevel()); // KillUserLevel
				packet.writeD(holder.getKillerRaceId()); // KillUserRace
				packet.writeD(holder.getKillerClassId()); // KillUserClass
				Player killer = World.getInstance().getPlayer(holder.getKillerName());
				packet.writeD((killer != null) && killer.isOnline() ? 2 : 0); // KillUserOnline (2 - online, 0 - offline)
				packet.writeD(0); // KillUserKarma
				packet.writeD((int) (holder.getShareTime() / 1000)); // nSharedTime
			}
		}
		return true;
	}
}
