package project.ksy.com.yuhancommunity.user.current.freeboard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import project.ksy.com.yuhancommunity.R;

/**
 * Created by kor on 2017-09-11.
 */

public class Freeboard_Adapter_list extends BaseAdapter {
    private ArrayList<Freeboard_item> mDataset;
    private Activity activity;
    private int iResource;

    public Freeboard_Adapter_list(ArrayList<Freeboard_item> mDataset, Activity activity, int iResource) {
        this.mDataset = mDataset;
        this.activity = activity;
        this.iResource = iResource;
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder vholder = null;
        if (view == null) {
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            view = layoutInflater.inflate(iResource, parent, false);
            TextView tvName = (TextView) view.findViewById(R.id.info_tv);
            TextView tvMajor = (TextView) view.findViewById(R.id.info_major);
            TextView tvComment = (TextView) view.findViewById(R.id.tv_comment);
            TextView commentBtn = (TextView) view.findViewById(R.id.commentBtn);
            ImageView imgboardPhoto = (ImageView) view.findViewById(R.id.main_img);
            ImageView imgProfile = (ImageView) view.findViewById(R.id.profile_img);
            TextView tvdate = (TextView) view.findViewById(R.id.date_tv);
            vholder = new ViewHolder(tvName, tvMajor, tvComment, imgboardPhoto, commentBtn, tvdate, imgProfile);
            view.setTag(vholder);
        } else {
            vholder = (ViewHolder) view.getTag();
        }
        Freeboard_item item = mDataset.get(position);
        vholder.tvName.setText(item.getName());
        vholder.tvMajor.setText(item.getMajor());
        vholder.tvComment.setText(item.getComment());
        vholder.tvdate.setText(item.getDate());
        Glide.with(activity).load(item.getProfile_photo()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(vholder.imgProfile);
        Glide.with(activity).load(item.getBoard_photo()).into(vholder.imgboardPhoto);

        vholder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "눌림", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public class ViewHolder {
        private TextView tvName;
        private TextView tvMajor;
        private TextView tvComment;
        private ImageView imgboardPhoto;
        private TextView commentBtn;
        private TextView tvdate;
        private ImageView imgProfile;

        public ViewHolder(TextView tvName, TextView tvMajor, TextView tvComment, ImageView imgboardPhoto, TextView commentBtn, TextView tvdate, ImageView imgProfile) {
            this.tvName = tvName;
            this.tvMajor = tvMajor;
            this.tvComment = tvComment;
            this.imgboardPhoto = imgboardPhoto;
            this.commentBtn = commentBtn;
            this.tvdate = tvdate;
            this.imgProfile = imgProfile;
        }
    }
}
