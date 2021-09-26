// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ChatRoomProtocol.proto

package optic_fusion1.client.protos;

public interface PacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:Packet)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>required .Packet.PacketType packet_type = 1;</code>
   * @return Whether the packetType field is set.
   */
  boolean hasPacketType();
  /**
   * <code>required .Packet.PacketType packet_type = 1;</code>
   * @return The packetType.
   */
  optic_fusion1.client.protos.Packet.PacketType getPacketType();

  /**
   * <code>required .ProtocolVersion protocol_version = 2;</code>
   * @return Whether the protocolVersion field is set.
   */
  boolean hasProtocolVersion();
  /**
   * <code>required .ProtocolVersion protocol_version = 2;</code>
   * @return The protocolVersion.
   */
  optic_fusion1.client.protos.ProtocolVersion getProtocolVersion();

  /**
   * <code>optional bool use_encryption = 3 [default = false];</code>
   * @return Whether the useEncryption field is set.
   */
  boolean hasUseEncryption();
  /**
   * <code>optional bool use_encryption = 3 [default = false];</code>
   * @return The useEncryption.
   */
  boolean getUseEncryption();
}