package edu.sdccd.cisc191.service;

import edu.sdccd.cisc191.grpc.GameServiceGrpc;
import edu.sdccd.cisc191.grpc.JoinMatchRequest;
import edu.sdccd.cisc191.grpc.JoinMatchResponse;
import edu.sdccd.cisc191.grpc.MatchHistoryRequest;
import edu.sdccd.cisc191.grpc.MatchHistoryResponse;
import edu.sdccd.cisc191.grpc.MatchResultResponse;
import edu.sdccd.cisc191.grpc.PlayMatchRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.concurrent.Task;

public class GameGrpcClient {

    private final ManagedChannel channel;
    private final GameServiceGrpc.GameServiceBlockingStub blockingStub;

    public GameGrpcClient(String host, int port) {
        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        this.blockingStub = GameServiceGrpc.newBlockingStub(channel);
    }

    public Task<JoinMatchResponse> joinMatchTask(
            String playerName,
            String difficulty,
            boolean ranked
    ) {
        return new Task<>() {
            @Override
            protected JoinMatchResponse call() {
                JoinMatchRequest request =
                        buildJoinMatchRequest(playerName, difficulty, ranked);

                return blockingStub.joinMatch(request);
            }
        };
    }

    public static JoinMatchRequest buildJoinMatchRequest(
            String playerName,
            String difficulty,
            boolean ranked
    ) {
        String resolvedPlayer =
                (playerName == null || playerName.isBlank())
                        ? "Player"
                        : playerName.trim();

        String resolvedDifficulty =
                (difficulty == null || difficulty.isBlank())
                        ? "Normal"
                        : difficulty.trim();

        return JoinMatchRequest.newBuilder()
                .setPlayerName(resolvedPlayer)
                .setDifficulty(resolvedDifficulty)
                .setRanked(ranked)
                .build();
    }

    public Task<MatchResultResponse> playMatchTask(
            String matchId,
            String playerName
    ) {
        return new Task<>() {
            @Override
            protected MatchResultResponse call() {

                PlayMatchRequest request = PlayMatchRequest.newBuilder()
                        .setMatchId(matchId)
                        .setPlayerName(
                                (playerName == null || playerName.isBlank())
                                        ? "Player"
                                        : playerName.trim()
                        )
                        .build();

                return blockingStub.playMatch(request);
            }
        };
    }

    public Task<MatchHistoryResponse> loadMatchHistoryTask(String playerName) {
        return new Task<>() {
            @Override
            protected MatchHistoryResponse call() {

                MatchHistoryRequest request = MatchHistoryRequest.newBuilder()
                        .setPlayerName(
                                (playerName == null || playerName.isBlank())
                                        ? "Player"
                                        : playerName.trim()
                        )
                        .build();

                return blockingStub.loadMatchHistory(request);
            }
        };
    }

    public void shutdown() {
        channel.shutdown();
    }
}