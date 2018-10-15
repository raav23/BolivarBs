package appr.softectachira.com.bolivarbs.slide1;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import appr.softectachira.com.bolivarbs.R;


public class SliderAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

//    private TextView slideHeading, slideDescription;
//    private ImageView slide_imageView;


    public SliderAdapter(Context context) {

        this.context = context;
    }

    // img Array
    public int[] image_slide ={
            R.drawable.circulobolivares,
            R.drawable.circuloretirarbs,
            R.drawable.circulodados


    };

    // heading Array
    public String[] heading_slide ={
            "GANA BOLIVARES",
            "RETIRA CUANDO QUIERAS",
            "COMO FUNCIONA"
    };

    // description Array
    public String[] description_slide ={
            "Puedes ganar bolívares de forma muy rápida, sencilla y divertida.  Gracias a nuestro sistema de repartición inteligente en un solo juego de dos minutos ya puedes ganar dinero ",
            "Dispones de tu dinero cuando quieras a través de los medios de pago Nacionales.",
            "El juego es muy simple: el jugador que saque el menor numero pierde . Sin embargo son 4 turnos por lo que tiene oportunidad de recuperarse e incluso de ganar   "
    };




    @Override
    public int getCount() {

        return heading_slide.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container,false);
        container.addView(view);

        ImageView slide_imageView = view.findViewById(R.id.imageView1);
        TextView slideHeading = view.findViewById(R.id.tvHeading);
        TextView  slideDescription = view.findViewById(R.id.tvDescription);

        slide_imageView.setImageResource(image_slide[position]);
        slideHeading.setText(heading_slide[position]);
        slideDescription.setText(description_slide[position]);

        return view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }

//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        View view = (View) object;
//        container.removeView(view);
//    }

}


