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
package org.l2jmobius.loginserver.network.serverpackets;

import org.l2jmobius.loginserver.SessionKey;
import org.l2jmobius.loginserver.network.AbstractServerPacket;

/**
 * Format: dddddddd f: the session key d: ? d: ? d: ? d: ? d: ? d: ?
 */
public class LoginOk extends AbstractServerPacket
{
	public LoginOk(SessionKey sessionKey)
	{
		writeByte(0x03);
		writeInt(sessionKey.loginOkID1);
		writeInt(sessionKey.loginOkID2);
		writeInt(0x00);
		writeInt(0x00);
		writeInt(0x000003ea);
		writeInt(0x00);
		writeInt(0x00);
		writeInt(0x02);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}