package rescuecore2.worldmodel.properties;

import com.google.protobuf.ByteString;
import rescuecore2.URN;
import rescuecore2.messages.protobuf.RCRSProto.PropertyProto;
import rescuecore2.worldmodel.AbstractProperty;
import rescuecore2.worldmodel.Property;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static rescuecore2.misc.EncodingTools.readInt32;
import static rescuecore2.misc.EncodingTools.writeInt32;

/**
 * A single String property.
 */
public class StringProperty extends AbstractProperty {
	private String value;

	/**
	 * Construct an StringProperty with no defined value.
	 *
	 * @param urn The urn of this property.
	 */
	public StringProperty(int urn) {
		super(urn);
	}

	/**
	 * Construct an IntProperty with no defined value.
	 *
	 * @param urn The urn of this property.
	 */
	public StringProperty(URN urn) {
		super(urn);
	}

	/**
	 * Construct an StringProperty with a defined value.
	 *
	 * @param urn   The urn of this property.
	 * @param value The initial value of the property.
	 */
	public StringProperty(int urn, String value) {
		super(urn, true);
		this.value = value;
	}

	/**
	 * Construct an StringProperty with a defined value.
	 *
	 * @param urn   The urn of this property.
	 * @param value The initial value of the property.
	 */
	public StringProperty(URN urn, String value) {
		super(urn, true);
		this.value = value;
	}

	/**
	 * StringProperty copy constructor.
	 *
	 * @param other The StringProperty to copy.
	 */
	public StringProperty(StringProperty other) {
		super(other);
		this.value = other.value;
	}

	@Override
	public String getValue() {
		if (!isDefined()) {
			return null;
		}
		return value;
	}

	/**
	 * Set the value of this property. Future calls to {@link #isDefined()} will
	 * return true.
	 * 
	 * @param value The new value.
	 */
	public void setValue(String value) {
		String old = this.value;
		boolean wasDefined = isDefined();
		this.value = value;
		setDefined();
		if (!wasDefined || old != value) {
			fireChange(old, value);
		}
	}

	@Override
	public void takeValue(Property p) {
		if (p instanceof StringProperty) {
			StringProperty i = (StringProperty) p;
			if (i.isDefined()) {
				setValue(i.getValue());
			} else {
				undefine();
			}
		} else {
			throw new IllegalArgumentException(
					this + " cannot take value from " + p);
		}
	}

	@Override
	public void write(OutputStream out) throws IOException {
		write(out);
	}

	@Override
	public void read(InputStream in) throws IOException {
		setValue(in.toString());
	}

	@Override
	public StringProperty copy() {
		return new StringProperty(this);
	}

	@Override
    public PropertyProto toPropertyProto() {
    	PropertyProto.Builder builder = basePropertyProto();
		if (isDefined()) {
			builder.setByteList(ByteString.copyFrom(value.getBytes(Charset.defaultCharset())));
		}
    	return builder.build();
    }
	@Override
	public void fromPropertyProto(PropertyProto proto) {
		if (!proto.getDefined())
			return;
		setValue(proto.getByteList().toString(Charset.defaultCharset()));
	}
}
