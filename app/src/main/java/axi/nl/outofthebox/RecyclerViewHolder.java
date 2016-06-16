package axi.nl.outofthebox;

/**
 * Created by rdkl on 2-6-2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    TextView tv1, tv2;
    ImageView imageView;
    Button btnPos, btnNeg;
    RelativeLayout background;

    public RecyclerViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;

        background = (RelativeLayout) itemView.findViewById(R.id.background);
        tv1 = (TextView) itemView.findViewById(R.id.list_title);
        tv2 = (TextView) itemView.findViewById(R.id.list_desc);
        btnPos = (Button) itemView.findViewById(R.id.btnPos);
        btnNeg = (Button) itemView.findViewById(R.id.btnNeg);

        imageView = (ImageView) itemView.findViewById(R.id.list_icon);


    }
}