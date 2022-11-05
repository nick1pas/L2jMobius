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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.loginserver.GameServerTable.GameServer;
import org.l2jmobius.loginserver.network.AbstractServerPacket;
import org.l2jmobius.loginserver.network.gameserverpackets.ServerStatus;

/**
 * ServerList Format: cc [cddcchhcdc] c: server list size (number of servers) c: ? [ (repeat for each servers) c: server id (ignored by client?) d: server ip d: server port c: age limit (used by client?) c: pvp or not (used by client?) h: current number of players h: max number of players c: 0 if
 * server is down d: 2nd bit: clock 3rd bit: wont dsiplay server name 4th bit: test server (used by client?) c: 0 if you dont want to display brackets in front of sever name ] Server will be considered as Good when the number of online players is less than half the maximum. as Normal between half
 * and 4/5 and Full when there's more than 4/5 of the maximum number of players
 */
public class ServerList extends AbstractServerPacket
{
	private final List<ServerData> _servers;
	
	private boolean _listDone = false;
	private final int _lastServer;
	
	class ServerData
	{
		protected String _ip;
		protected int _port;
		protected boolean _pvp;
		protected int _currentPlayers;
		protected int _maxPlayers;
		protected boolean _testServer;
		protected boolean _brackets;
		protected boolean _clock;
		protected int _status;
		protected int _serverId;
		
		ServerData(String pIp, GameServer gs, int pStatus)
		{
			_ip = pIp;
			_port = gs.port;
			_pvp = gs.pvp;
			_testServer = gs.testServer;
			_currentPlayers = (gs.gst == null ? 0 : gs.gst.getCurrentPlayers());
			_maxPlayers = gs.maxPlayers;
			_brackets = gs.brackets;
			_clock = gs.clock;
			_status = pStatus;
			_serverId = gs.serverId;
		}
	}
	
	public ServerList(int lastServer)
	{
		_lastServer = lastServer;
		_servers = new ArrayList<>();
	}
	
	public void addServer(String ip, GameServer game, int status)
	{
		_servers.add(new ServerData(ip, game, status));
	}
	
	@Override
	public byte[] getContent()
	{
		if (!_listDone) // list should only be done once even if there are multiple getContent calls
		{
			writeByte(0x04);
			writeByte(_servers.size());
			writeByte(_lastServer);
			
			for (ServerData server : _servers)
			{
				writeByte(server._serverId); // server id
				try
				{
					final InetAddress i4 = InetAddress.getByName(server._ip);
					final byte[] raw = i4.getAddress();
					writeByte(raw[0] & 0xff);
					writeByte(raw[1] & 0xff);
					writeByte(raw[2] & 0xff);
					writeByte(raw[3] & 0xff);
				}
				catch (UnknownHostException e)
				{
					e.printStackTrace();
					writeByte(127);
					writeByte(0);
					writeByte(0);
					writeByte(1);
				}
				
				writeInt(server._port);
				writeByte(0x00); // age limit
				writeByte(server._pvp ? 0x01 : 0x00);
				
				writeShort(server._currentPlayers);
				writeShort(server._maxPlayers);
				writeByte(server._status == ServerStatus.STATUS_DOWN ? 0x00 : 0x01);
				
				int bits = 0;
				if (server._testServer)
				{
					bits |= 0x04;
				}
				
				if (server._clock)
				{
					bits |= 0x02;
				}
				
				writeInt(bits);
				writeByte(server._brackets ? 0x01 : 0x00);
			}
			_listDone = true;
		}
		
		return getBytes();
	}
}