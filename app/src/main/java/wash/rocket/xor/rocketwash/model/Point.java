package wash.rocket.xor.rocketwash.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Point implements Parcelable
{

	public double lon = 0;
	public double lat = 0;

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeDouble(lon);
		dest.writeDouble(lat);
	}

	public Point()
	{

	}

	public Point(Parcel in)
	{
		lon = in.readDouble();
		lat = in.readDouble();
	}

	public static final Creator<Point> CREATOR = new Creator<Point>()
	{
		public Point createFromParcel(Parcel in)
		{
			return new Point(in);
		}

		public Point[] newArray(int size)
		{
			return new Point[size];
		}
	};

}
