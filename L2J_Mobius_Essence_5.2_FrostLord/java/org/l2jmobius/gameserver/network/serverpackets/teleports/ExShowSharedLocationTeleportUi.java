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
package org.l2jmobius.gameserver.network.serverpackets.teleports;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.holders.SharedTeleportHolder;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author NasSeKa
 */
public class ExShowSharedLocationTeleportUi implements IClientOutgoingPacket
{
	private final SharedTeleportHolder _teleport;
	
	public ExShowSharedLocationTeleportUi(SharedTeleportHolder teleport)
	{
		_teleport = teleport;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHARED_POSITION_TELEPORT_UI.writeId(packet);
		packet.writeString(_teleport.getName());
		packet.writeD(_teleport.getId());
		packet.writeD(_teleport.getCount());
		packet.writeH(150);
		packet.writeD(_teleport.getLocation().getX());
		packet.writeD(_teleport.getLocation().getY());
		packet.writeD(_teleport.getLocation().getZ());
		return true;
	}
}
