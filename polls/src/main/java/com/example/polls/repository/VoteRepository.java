package com.example.polls.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.polls.dto.response.ChoiceVoteCount;
import com.example.polls.entity.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{
  
  @Query("SELECT NEW com.example.polls.dto.response.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id in :pollIds GROUP BY v.choice.id")
  List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollIds") List<Long> pollIds);
}
