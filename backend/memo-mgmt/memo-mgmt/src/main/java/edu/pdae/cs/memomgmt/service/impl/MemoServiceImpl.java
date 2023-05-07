package edu.pdae.cs.memomgmt.service.impl;

import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.MemoDTO;
import edu.pdae.cs.memomgmt.repository.MemoRepository;
import edu.pdae.cs.memomgmt.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MemoRepository memoRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<MemoDTO> getAllByVisibility(Memo.Visibility visibility, String asUser) {
        if (Memo.Visibility.PRIVATE.equals(visibility)) {
            return memoRepository.getMemosByAuthorAndVisibility(asUser, visibility).stream().map(memo -> modelMapper.map(memo, MemoDTO.class)).toList();
        }

        return memoRepository.getMemosByVisibility(visibility).stream().map(memo -> modelMapper.map(memo, MemoDTO.class)).toList();
    }

    @Override
    public List<MemoDTO> getAllAfter(Date after, String asUser) {
        return memoRepository.getMemosByCreatedOnAfter(after).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    public List<MemoDTO> getAllAfterAndByVisibility(Date after, Memo.Visibility visibility, String asUser) {
        if (Memo.Visibility.PRIVATE.equals(visibility)) {
            return memoRepository.getMemosByCreatedOnAfterAndVisibilityAndAuthor(after, visibility, asUser).stream().map(memo -> modelMapper.map(memo, MemoDTO.class)).toList();
        }

        return memoRepository.getMemosByCreatedOnAfterAndVisibility(after, visibility).stream().map(memo -> modelMapper.map(memo, MemoDTO.class)).toList();
    }

    @Override
    public List<MemoDTO> getAll(String asUser) {
        return memoRepository.findAll().stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    public List<MemoDTO> getAllAfterByHubIdAndByVisibility(Date after, Memo.Visibility visibility, ObjectId hubId, String asUser) {
        return memoRepository.getMemosByCreatedOnAfterAndHubIdAndVisibility(after, hubId, visibility).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    public List<MemoDTO> getAllAfterByHubId(Date after, ObjectId hubId, String asUser) {
        return memoRepository.getMemosByCreatedOnAfterAndHubId(after, hubId).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    public List<MemoDTO> getAllByHubIdAndByVisibility(ObjectId hubId, Memo.Visibility visibility, String asUser) {
        return memoRepository.getMemosByHubIdAndVisibility(hubId, visibility).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    public List<MemoDTO> getAllByHubId(ObjectId hubId, String asUser) {
        return memoRepository.getMemosByHubId(hubId).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

}
