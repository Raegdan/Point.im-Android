package org.itishka.pointim;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.itishka.pointim.api.data.Comment;
import org.itishka.pointim.api.data.ExtendedPost;

import java.lang.ref.WeakReference;

/**
 * Created by Татьяна on 20.10.2014.
 */
public class SinglePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ExtendedPost mPost;
    private final WeakReference<Context> mContext;

    @Override
    public int getItemViewType(int position) {
        if (position==0)
            return 0;
        else
            return 1;
    }

    public Object getItem(int pos) {
        if (pos==0)
            return mPost;
        else
            return mPost.comments[pos-1];
    }

    protected class PostViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewGroup tags;
        ImageView avatar;
        TextView author;
        TextView post_id;
        TextView comments;
        TextView date;
        ImageView webLink;
        CheckBox favourite;
        View mainConent;
        public PostViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            tags = (ViewGroup) itemView.findViewById(R.id.tags);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            author = (TextView) itemView.findViewById(R.id.author);
            post_id = (TextView) itemView.findViewById(R.id.post_id);
            comments = (TextView) itemView.findViewById(R.id.comments);
            date = (TextView) itemView.findViewById(R.id.date);
            webLink = (ImageView) itemView.findViewById(R.id.weblink);
            favourite = (CheckBox) itemView.findViewById(R.id.favourite);
            mainConent = itemView.findViewById(R.id.main_content);
        }
    }
    protected class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView avatar;
        TextView author;
        TextView date;
        View divider;
        TextView comment_id;
        public CommentViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
            divider = itemView.findViewById(R.id.divider);
            comment_id = (TextView)itemView.findViewById(R.id.comment_id);
        }
    }

    public void setData(ExtendedPost post) {
        mPost = post;
        notifyDataSetChanged();
    }
    public SinglePostAdapter(Context context, ExtendedPost post){
        super();
        mPost = post;
        mContext = new WeakReference<Context>(context);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i==0) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.single_post_header, viewGroup, false);
            final PostViewHolder holder = new PostViewHolder(v);
            holder.webLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, (Uri)view.getTag());
                    getContext().startActivity(browserIntent);
                }
            });
            holder.post_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(getContext(), SinglePostActivity.class);
                    browserIntent.putExtra("post", view.getTag().toString());
                    getContext().startActivity(browserIntent);
                }
            });
            return holder;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_comment, viewGroup, false);
            final CommentViewHolder holder = new CommentViewHolder(v);
            return holder;
        }

    }

    private Context getContext() {
        return mContext.get();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (i==0) {
            PostViewHolder postHolder = (PostViewHolder) holder;
            postHolder.author.setText("@" + mPost.post.author.login);
            postHolder.text.setText(mPost.post.text);
            Utils.showAvatar(getContext(), mPost.post.author.avatar, postHolder.avatar);
            postHolder.date.setText(Utils.formatDate(mPost.post.created));

            postHolder.post_id.setText("#" + mPost.post.id);
            postHolder.post_id.setTag(mPost.post.id);
            postHolder.webLink.setTag(Utils.getnerateSiteUri(mPost.post.id));
            //postHolder.favourite.setChecked(mPost.);
            //postHolder.favourite.setTag(mPost.post.id);

            if (mPost.post.comments_count > 0) {
                postHolder.comments.setText(String.valueOf(mPost.post.comments_count));
            } else {
                postHolder.comments.setText("");
            }
            postHolder.tags.removeAllViews();
            if (mPost.post.tags.length == 0) {
                postHolder.tags.setVisibility(View.GONE);
            } else {
                postHolder.tags.setVisibility(View.VISIBLE);
                for (String tag : mPost.post.tags) {
                    LayoutInflater li;
                    li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final TextView v = (TextView) li.inflate(R.layout.tag, null);
                    v.setText(tag);
                    postHolder.tags.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        } else {
            Comment comment = mPost.comments[i-1];
            CommentViewHolder commentHolder = (CommentViewHolder) holder;
            Utils.showAvatar(getContext(), comment.author.avatar, commentHolder.avatar);
            if (i==1) {
                commentHolder.divider.setVisibility(View.INVISIBLE);
            } else {
                commentHolder.divider.setVisibility(View.VISIBLE);
            }
            commentHolder.date.setText(Utils.formatDate(comment.created));
            commentHolder.text.setText(comment.text);
            commentHolder.author.setText(comment.author.login);
            if (TextUtils.isEmpty(comment.to_comment_id))
                commentHolder.comment_id.setText("/"+comment.id);
            else
                commentHolder.comment_id.setText("/"+comment.id+" → "+"/"+comment.to_comment_id);
        }
    }

    @Override
    public int getItemCount() {
        if (mPost==null)
            return 0;
        else return mPost.comments.length+1;
    }
}
