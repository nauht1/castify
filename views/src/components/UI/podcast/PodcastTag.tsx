import React from 'react';
import { Podcast } from '../../../models/PodcastModel';
import { formatDistanceToNow } from 'date-fns';
import default_avatar from '../../../assets/images/default_avatar.jpg';
import { Link } from 'react-router-dom';

interface PodcastTagProps {
  podcast: Podcast;
}

const PodcastTag: React.FC<PodcastTagProps> = ({ podcast }) => {
  const author = podcast.user.fullname;
  const createdDay = formatDistanceToNow(new Date(podcast.createdDay), { addSuffix: true });

  return (
    <div className="rounded-lg overflow-hidden transform transition-transform duration-300">
      <Link to={`/watch?pid=${podcast.id}`}>
        <img src={podcast.thumbnailUrl || "/TEST.png"} alt={podcast.title} className="w-full h-56 object-fit" />
      </Link>
      <div className="p-2 flex">
        <Link to={`/profile/${podcast.user.username}`}>
          <img src={podcast.user.avatarUrl || default_avatar} alt={author} className="w-10 h-10 object-cover rounded-full mr-4" />
        </Link>
        <div className="flex flex-col justify-between">
          <div>
            <Link to={`/watch?pid=${podcast.id}`}>
              <h3 className="text-lg font-bold line-clamp-2 text-black dark:text-white" title={podcast.title}>{podcast.title}</h3>
            </Link>
            <Link to={`/profile/${podcast.user.username}`}>
              <p className="text-base font-medium text-gray-800 dark:text-white">{author}</p>
            </Link>
          </div>
          <div className="flex gap-4 text-base text-gray-800 dark:text-gray-300">
            <p>{podcast.views} views</p>
            <p>{createdDay}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PodcastTag;