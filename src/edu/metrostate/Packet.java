package edu.metrostate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Packet implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private short cksum; //16-bit 2-byte
	private short len = 512;	//16-bit 2-byte
	private int ackno;	//32-bit 4-byte
	private int seqno; 	//32-bit 4-byte Data packet Only
	private byte data[] = new byte[500]; //0-500 bytes. Data packet only. Variable
	
	// Constructor for Data Packets
	public Packet(short cksum, short len, int ackno, int seqno, byte[] data) {
		this.cksum = cksum;
		this.len = len;
		this.ackno = ackno;
		this.seqno = seqno;
		this.data = data;
	}
	//Constructor for Ack Packets
	public Packet(short cksum, short len, int ackno) {
		this.cksum = cksum;
		this.len = len;
		this.ackno = ackno;
	}
	public Packet() {
	}
	
	/**
	 * This simulates a lossy network per assignment instructions
	 * @param packet
	 * @return packet condition
	 * @throws InterruptedException
	 */
	public String simLossyNetwork(Packet packet) throws InterruptedException {
		final int CORRUPT = 0;
		final int DELAY = 1;
		final int DROP = 2;
		Random number = new Random();
		float corruptDatagramsRatio;
		
		if (packet.len == 8) { // Ack packet
			corruptDatagramsRatio = Receiver.corruptDatagramsRatio;
		} else {
			corruptDatagramsRatio = Sender.corruptDatagramsRatio;
		}
		
		if (number.nextFloat() < corruptDatagramsRatio) { // Corrupt
			int random = number.nextInt(3);
			switch(random) {
				case CORRUPT: this.cksum = 1;
					return "ERRR";
				case DELAY:
					return "DLYD";
				case DROP:
					return "DROP";
			}
		}
		return "SENT";
	}
	/**
	 * Converts packet to byte[] for Datagram transport
	 * @param packet
	 * @return
	 * @throws IOException
	 */
	public byte[] convertToBytes(Packet packet) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(Sender.size);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(packet);
		byte[] data = baos.toByteArray();
		return data;
	}
	/**
	 * Converts byte[] back into packet for Datagram transport
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public Packet convertToPacket(byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
		try {
			Packet packet = (Packet) is.readObject();
			return packet;
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getCurrentTime() {
		Date date= new Date();
		long time = date.getTime();
		Timestamp timestamp = new Timestamp(time);
		return timestamp.toString().substring(11);
	}

	public short getCksum() {
		return cksum;
	}

	public void setCksum(short cksum) {
		this.cksum = cksum;
	}

	public short getLen() {
		return len;
	}

	public void setLen(short len) {
		this.len = len;
	}

	public int getAckno() {
		return ackno;
	}

	public void setAckno(int ackno) {
		this.ackno = ackno;
	}

	public int getSeqno() {
		return seqno;
	}

	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String getPayload() throws UnsupportedEncodingException {
		String payload = new String(data, "UTF-8");
		return payload;
	}

	@Override
	public String toString() {
		return "Packet [cksum=" + cksum + ", len=" + len + ", ackno=" + ackno
				+ ", seqno=" + seqno + ", data=" + Arrays.toString(data) + "]";
	}
}
