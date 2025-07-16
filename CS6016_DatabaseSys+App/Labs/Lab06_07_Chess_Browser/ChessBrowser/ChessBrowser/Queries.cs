using Microsoft.Maui.Controls;
using MySqlConnector;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace ChessBrowser
{
  internal class Queries
  {

    /// <summary>
    /// This function runs when the upload button is pressed.
    /// Given a filename, parses the PGN file, and uploads
    /// each chess game to the user's database.
    /// </summary>
    /// <param name="PGNfilename">The path to the PGN file</param>
    internal static async Task InsertGameData(string PGNfilename, MainPage mainPage)
    {
      // This will build a connection string to your user's database on atr,
      // assuimg you've typed a user and password in the GUI
      string connection = mainPage.GetConnectionString();

      // TODO:
      //       Load and parse the PGN file
      //       We recommend creating separate libraries to represent chess data and load the file

      string content = File.ReadAllText(PGNfilename);
      string[] games = content.Split(new string[] { "\r\n\r\n", "\n\n" }, StringSplitOptions.RemoveEmptyEntries);

      // TODO:
      //       Use this to tell the GUI's progress bar how many total work steps there are
      //       For example, one iteration of your main upload loop could be one work step
      //mainPage.SetNumWorkItems( ... );

      mainPage.SetNumWorkItems(games.Length);


      using (MySqlConnection conn = new MySqlConnection(connection))
      {
        try
        {
          // Open a connection
          await conn.OpenAsync();
          // TODO:
          //       iterate through your data and generate appropriate insert commands
          foreach (string gameText in games)
          {
            if (string.IsNullOrWhiteSpace(gameText)) continue;

            // Parse game data
            var eventName = ExtractTag(gameText, "Event");
            var site = ExtractTag(gameText, "Site");
            var round = ExtractTag(gameText, "Round");
            var white = ExtractTag(gameText, "White");
            var black = ExtractTag(gameText, "Black");
            var result = ConvertResult(ExtractTag(gameText, "Result"));
            var eventDate = ParseDate(ExtractTag(gameText, "EventDate"));
            var whiteElo = TryParseInt(ExtractTag(gameText, "WhiteElo"));
            var blackElo = TryParseInt(ExtractTag(gameText, "BlackElo"));
            var moves = ExtractMoves(gameText);

            // Debug: Check if moves are being extracted
            System.Diagnostics.Debug.WriteLine($"Extracted moves for {white} vs {black}: '{moves.Substring(0, Math.Min(100, moves.Length))}...'");

            // Insert players
            int whiteId = await InsertOrGetPlayer(conn, white, whiteElo);
            int blackId = await InsertOrGetPlayer(conn, black, blackElo);

            // Insert event
            int eventId = await InsertOrGetEvent(conn, eventName, site, eventDate);

            // Insert game
            var cmd = conn.CreateCommand();
            cmd.CommandText = @"INSERT INTO Games (Round, Result, Moves, BlackPlayer, WhitePlayer, eID) 
                               VALUES (@round, @result, @moves, @black, @white, @event)
                               ON DUPLICATE KEY UPDATE Result = VALUES(Result), Moves = VALUES(Moves)";
            cmd.Parameters.AddWithValue("@round", round);
            cmd.Parameters.AddWithValue("@result", result);
            cmd.Parameters.AddWithValue("@moves", moves);
            cmd.Parameters.AddWithValue("@black", blackId);
            cmd.Parameters.AddWithValue("@white", whiteId);
            cmd.Parameters.AddWithValue("@event", eventId);
            await cmd.ExecuteNonQueryAsync();

            // TODO:
            //       Use this inside a loop to tell the GUI that one work step has completed:
            await mainPage.NotifyWorkItemCompleted();
          }
        }
        catch (Exception e)
        {
          System.Diagnostics.Debug.WriteLine(e.Message);
        }
      }

    }


    /// <summary>
    /// Queries the database for games that match all the given filters.
    /// The filters are taken from the various controls in the GUI.
    /// </summary>
    /// <param name="white">The white player, or null if none</param>
    /// <param name="black">The black player, or null if none</param>
    /// <param name="opening">The first move, e.g. "1.e4", or null if none</param>
    /// <param name="winner">The winner as "W", "B", "D", or null if none</param>
    /// <param name="useDate">True if the filter includes a date range, False otherwise</param>
    /// <param name="start">The start of the date range</param>
    /// <param name="end">The end of the date range</param>
    /// <param name="showMoves">True if the returned data should include the PGN moves</param>
    /// <returns>A string separated by newlines containing the filtered games</returns>
    internal static string PerformQuery(string white, string black, string opening,
      string winner, bool useDate, DateTime start, DateTime end, bool showMoves,
      MainPage mainPage)
    {
      // This will build a connection string to your user's database on atr,
      // assuimg you've typed a user and password in the GUI
      string connection = mainPage.GetConnectionString();

      // Build up this string containing the results from your query
      string parsedResult = "";

      // Use this to count the number of rows returned by your query
      // (see below return statement)
      int numRows = 0;

      using (MySqlConnection conn = new MySqlConnection(connection))
      {
        try
        {
          // Open a connection
          conn.Open();

          // TODO:
          //       Generate and execute an SQL command,
          //       then parse the results into an appropriate string and return it.
          string query = @"SELECT e.Name, e.Site, e.Date, g.Round, wp.Name as White, bp.Name as Black, g.Result";
          if (showMoves) query += ", g.Moves";
          query += @" FROM Games g 
                     JOIN Events e ON g.eID = e.eID 
                     JOIN Players wp ON g.WhitePlayer = wp.pID 
                     JOIN Players bp ON g.BlackPlayer = bp.pID 
                     WHERE 1=1";

          var cmd = conn.CreateCommand();
          cmd.CommandText = query;

          if (!string.IsNullOrEmpty(white))
          {
            cmd.CommandText += " AND wp.Name LIKE @white";
            cmd.Parameters.AddWithValue("@white", "%" + white + "%");
          }
          if (!string.IsNullOrEmpty(black))
          {
            cmd.CommandText += " AND bp.Name LIKE @black";
            cmd.Parameters.AddWithValue("@black", "%" + black + "%");
          }
          if (!string.IsNullOrEmpty(opening))
          {
            cmd.CommandText += " AND g.Moves LIKE @opening";
            cmd.Parameters.AddWithValue("@opening", opening + "%");
          }
          if (!string.IsNullOrEmpty(winner))
          {
            cmd.CommandText += " AND g.Result = @winner";
            cmd.Parameters.AddWithValue("@winner", winner);
          }
          if (useDate)
          {
            cmd.CommandText += " AND e.Date BETWEEN @start AND @end";
            cmd.Parameters.AddWithValue("@start", start);
            cmd.Parameters.AddWithValue("@end", end);
          }

          // Debug output
          System.Diagnostics.Debug.WriteLine($"SQL Query: {cmd.CommandText}");
          System.Diagnostics.Debug.WriteLine($"showMoves: {showMoves}");

          using var reader = cmd.ExecuteReader();
          while (reader.Read())
          {
            numRows++;
            parsedResult += $"Event: {reader["Name"]}\n";
            parsedResult += $"Site: {reader["Site"]}\n";
            parsedResult += $"Date: {reader["Date"]}\n";
            parsedResult += $"Round: {reader["Round"]}\n";
            parsedResult += $"White: {reader["White"]}\n";
            parsedResult += $"Black: {reader["Black"]}\n";
            parsedResult += $"Result: {FormatResult(reader["Result"].ToString())}\n";
            if (showMoves)
            {
              var movesValue = reader["Moves"]?.ToString() ?? "";
              System.Diagnostics.Debug.WriteLine($"Moves from DB (length {movesValue.Length}): '{movesValue.Substring(0, Math.Min(100, movesValue.Length))}...'");
              parsedResult += $"Moves: {movesValue}\n";
            }
            parsedResult += "------------------------\n";
          }
        }
        catch (Exception e)
        {
          System.Diagnostics.Debug.WriteLine(e.Message);
        }
      }

      return numRows + " results\n" + parsedResult;
    }
    // Helper methods
    private static string ExtractTag(string gameText, string tagName)
    {
      var match = Regex.Match(gameText, $@"\[{tagName}\s+""([^""]*)""\]");
      return match.Success ? match.Groups[1].Value : "";
    }

    private static string ConvertResult(string result)
    {
      return result switch
      {
        "1-0" => "W",
        "0-1" => "B",
        "1/2-1/2" => "D",
        _ => "D"
      };
    }

    private static string FormatResult(string result)
    {
      return result switch
      {
        "W" => "1-0 (White wins)",
        "B" => "0-1 (Black wins)",
        "D" => "1/2-1/2 (Draw)",
        _ => result
      };
    }

    private static DateTime ParseDate(string dateStr)
    {
      if (string.IsNullOrEmpty(dateStr) || dateStr.Contains("??"))
        return new DateTime(1, 1, 1);

      if (DateTime.TryParseExact(dateStr, "yyyy.MM.dd", null, System.Globalization.DateTimeStyles.None, out DateTime date))
        return date;

      return new DateTime(1, 1, 1);
    }

    private static int? TryParseInt(string str)
    {
      return int.TryParse(str, out int val) ? val : null;
    }

    private static string ExtractMoves(string gameText)
    {
      // Split the game text into lines
      var lines = gameText.Split(new char[] { '\n', '\r' }, StringSplitOptions.RemoveEmptyEntries);
      var moves = new StringBuilder();
      
      foreach (var line in lines)
      {
        var trimmedLine = line.Trim();
        
        // Skip empty lines and header lines (lines starting with [)
        if (string.IsNullOrWhiteSpace(trimmedLine) || trimmedLine.StartsWith("["))
          continue;
        
        // This should be a moves line - add it
        moves.AppendLine(trimmedLine);
      }
      
      return moves.ToString().Trim();
    }

    private static async Task<int> InsertOrGetPlayer(MySqlConnection conn, string name, int? elo)
    {
      var cmd = conn.CreateCommand();
      cmd.CommandText = "SELECT pID FROM Players WHERE Name = @name";
      cmd.Parameters.AddWithValue("@name", name);

      var result = await cmd.ExecuteScalarAsync();
      if (result != null)
      {
        int playerId = Convert.ToInt32(result);
        if (elo.HasValue)
        {
          cmd = conn.CreateCommand();
          cmd.CommandText = "UPDATE Players SET Elo = @elo WHERE pID = @id";
          cmd.Parameters.AddWithValue("@elo", elo.Value);
          cmd.Parameters.AddWithValue("@id", playerId);
          await cmd.ExecuteNonQueryAsync();
        }
        return playerId;
      }
      cmd = conn.CreateCommand();
      cmd.CommandText = "INSERT INTO Players (Name, Elo) VALUES (@name, @elo)";
      cmd.Parameters.AddWithValue("@name", name);
      cmd.Parameters.AddWithValue("@elo", elo.HasValue ? elo.Value : DBNull.Value);
      await cmd.ExecuteNonQueryAsync();
      return (int)cmd.LastInsertedId;
    }

    private static async Task<int> InsertOrGetEvent(MySqlConnection conn, string name, string site, DateTime date)
    {
      var cmd = conn.CreateCommand();
      cmd.CommandText = "SELECT eID FROM Events WHERE Name = @name AND Site = @site AND Date = @date";
      cmd.Parameters.AddWithValue("@name", name);
      cmd.Parameters.AddWithValue("@site", site);
      cmd.Parameters.AddWithValue("@date", date);

      var result = await cmd.ExecuteScalarAsync();
      if (result != null)
        return Convert.ToInt32(result);

      cmd = conn.CreateCommand();
      cmd.CommandText = "INSERT INTO Events (Name, Site, Date) VALUES (@name, @site, @date)";
      cmd.Parameters.AddWithValue("@name", name);
      cmd.Parameters.AddWithValue("@site", site);
      cmd.Parameters.AddWithValue("@date", date);
      await cmd.ExecuteNonQueryAsync();
      return (int)cmd.LastInsertedId;
    }

    // Test method to verify PGN parsing works
    internal static void TestPgnReader(string pgnFilename)
    {
      try
      {
        string content = File.ReadAllText(pgnFilename);
        string[] games = content.Split(new string[] { "\r\n\r\n", "\n\n" }, StringSplitOptions.RemoveEmptyEntries);

        System.Diagnostics.Debug.WriteLine($"Found {games.Length} games in file");

        // Test first few games
        for (int i = 0; i < Math.Min(3, games.Length); i++)
        {
          string gameText = games[i];
          if (string.IsNullOrWhiteSpace(gameText)) continue;

          System.Diagnostics.Debug.WriteLine($"\n--- Game {i + 1} ---");
          System.Diagnostics.Debug.WriteLine($"Event: '{ExtractTag(gameText, "Event")}'");
          System.Diagnostics.Debug.WriteLine($"Site: '{ExtractTag(gameText, "Site")}'");
          System.Diagnostics.Debug.WriteLine($"Round: '{ExtractTag(gameText, "Round")}'");
          System.Diagnostics.Debug.WriteLine($"White: '{ExtractTag(gameText, "White")}'");
          System.Diagnostics.Debug.WriteLine($"Black: '{ExtractTag(gameText, "Black")}'");
          System.Diagnostics.Debug.WriteLine($"Result: '{ExtractTag(gameText, "Result")}' -> '{ConvertResult(ExtractTag(gameText, "Result"))}'");
          System.Diagnostics.Debug.WriteLine($"EventDate: '{ExtractTag(gameText, "EventDate")}'");
          System.Diagnostics.Debug.WriteLine($"WhiteElo: '{ExtractTag(gameText, "WhiteElo")}'");
          System.Diagnostics.Debug.WriteLine($"BlackElo: '{ExtractTag(gameText, "BlackElo")}'");

          string moves = ExtractMoves(gameText);
          System.Diagnostics.Debug.WriteLine($"Moves (first 100 chars): '{moves.Substring(0, Math.Min(100, moves.Length))}...'");
        }
      }
      catch (Exception e)
      {
        System.Diagnostics.Debug.WriteLine($"Error testing PGN reader: {e.Message}");
      }
    }
  }
}